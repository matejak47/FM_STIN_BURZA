package com.example.burza.service;

import com.example.burza.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Service handling portfolio operations.
 * Manages user portfolios and executes trading transactions.
 */
@Service
@Data
public class PortfolioService {
    private final Portfolio portfolio;
    private final StockService stockService;
    @Value("${NewsUrl:https://stin-zpravy-hjdkcwh3fefhe8gv.germanywestcentral-01.azurewebsites.net}")
    private String newsUrl;
    @Value("${Tolerance:0.0}")
    private double tolerance;
    private final RestTemplate restTemplate;
    boolean testMode = false;
    private final LoggingService loggingService;


    /**
     * Constructor initializing portfolio and stock service.
     *
     * @param stockService service for fetching stock data
     */
    @Autowired
    public PortfolioService(StockService stockService, RestTemplate restTemplate, LoggingService loggingService) {
        this.stockService = stockService;
        this.portfolio = new Portfolio();
        this.restTemplate = restTemplate;
        this.loggingService = loggingService;
    }

    public void transaction(List<Symbol> filteredSymbols) throws InterruptedException {
        int id = sendDataToGrancek(filteredSymbols);
        String receivedJson = receiveDataFromGrancek(id);
        evaluateDataFromGrancek(receivedJson);
    }

    int sendDataToGrancek(List<Symbol> filteredSymbols) {
        if (filteredSymbols == null || filteredSymbols.isEmpty()) {
            throw new IllegalStateException("No filtered stocks available to send.");
        }

        String SendJson = parseFavoritesToJsonGrancek(filteredSymbols); // POZOR, tady změna - používáme vyfiltrované symboly!
        loggingService.log("Preparing to send JSON to Grancek: " + SendJson);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(SendJson, headers);
        String url = newsUrl + "/submit";
        loggingService.log("Sending POST request to URL: " + url);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
        );

        assert response.getBody() != null;
        loggingService.log("Received response body: " + response.getBody());
        return extractRequestId(response.getBody());
    }


    String receiveDataFromGrancek(int request_id) throws InterruptedException {
        String url = newsUrl + "/output/" + request_id + "/status";
        loggingService.log(url);
        boolean running = true;
        while (running) {
            String ReceiveJson = restTemplate.getForObject(url, String.class);
            loggingService.log(ReceiveJson);
            assert ReceiveJson != null;
            if (extractStatus(ReceiveJson).equals("done")) {
                loggingService.log("Done");
                running = false;
                continue;
            }

            if (!testMode) {
                Thread.sleep(2000);
            }
        }
        String ReceiveJson = restTemplate.getForObject(newsUrl + "/output/" + request_id, String.class);
        loggingService.log("ReceiveJson: " + ReceiveJson);
        return ReceiveJson;
    }

    private void evaluateDataFromGrancek(String receivedJson) {
        String outputJson = convertJsonStringToStatusJson(receivedJson);
        loggingService.log("OutputJson: " + outputJson);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(outputJson, headers);
        String url = newsUrl + "/UI";

        restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
        );
        loggingService.log(request.getBody());
    }

    public void enableTestMode() {
        this.testMode = true;
    }


    public List<StockResponse> parseToJson(List<String> symbols) {
        List<StockResponse> responseList = new ArrayList<>();
        try {
            for (String symbol : symbols) {
                List<DailyData> dailyData = stockService.fetchDailyTimeSeries(symbol);

                if (dailyData.isEmpty()) continue;  // Ignore empty data
                long timestamp = System.currentTimeMillis(); // Convert date to timestamp

                int rating = 0;
                int sell = 0;

                responseList.add(new StockResponse(symbol, timestamp, rating, sell));
            }
        } catch (Exception e) {
            System.err.println("Error fetching stock ratings: " + e.getMessage());
        }
        return responseList;
    }


    /**
     * Executes a trade order based on current stock prices.
     *
     * @param order the trade order
     * @return the result of the trade execution
     */
    public TradeResult executeTrade(TradeOrder order) {
        if (order.getOrderType() == null) {
            return new TradeResult(false, "Invalid trade order type", 0, 0, portfolio.getBalance());
        }

        List<DailyData> dailyData = stockService.fetchDailyTimeSeries(order.getSymbol());
        if (dailyData.isEmpty()) {
            return new TradeResult(false, "Impossible to get the current value of the stock", 0, 0, portfolio.getBalance());
        }

        double currentPrice = dailyData.get(0).getClose();

        TradeResult result;
        if (order.getOrderType() == TradeOrder.OrderType.BUY) {
            result = executeBuy(order, currentPrice);
        } else {
            result = executeSell(order, currentPrice);
        }

        savePortfolioState();
        return result;
    }

    private TradeResult executeBuy(TradeOrder order, double currentPrice) {
        double totalCost = currentPrice * order.getQuantity();
        if (totalCost > portfolio.getBalance()) {
            return new TradeResult(false, "Not enough resources for this purchase", currentPrice, totalCost, portfolio.getBalance());
        }

        int currentQuantity = portfolio.getHoldings().getOrDefault(order.getSymbol(), 0);
        portfolio.getHoldings().put(order.getSymbol(), currentQuantity + order.getQuantity());
        portfolio.setBalance(portfolio.getBalance() - totalCost);
        savePortfolioState();
        return new TradeResult(true, "Purchase successful", currentPrice, totalCost, portfolio.getBalance());
    }

    private TradeResult executeSell(TradeOrder order, double currentPrice) {
        int currentQuantity = portfolio.getHoldings().getOrDefault(order.getSymbol(), 0);
        if (currentQuantity < order.getQuantity()) {
            return new TradeResult(false, "Not enough stocks for the purchase", currentPrice, 0, portfolio.getBalance());
        }

        double totalValue = currentPrice * order.getQuantity();
        int newQuantity = currentQuantity - order.getQuantity();

        if (newQuantity > 0) {
            portfolio.getHoldings().put(order.getSymbol(), newQuantity);
        } else {
            portfolio.getHoldings().remove(order.getSymbol());
        }

        portfolio.setBalance(portfolio.getBalance() + totalValue);

        savePortfolioState();

        return new TradeResult(true, "Sale successful", currentPrice, totalValue, portfolio.getBalance());
    }

    private void savePortfolioState() {
        System.out.println("Saving portfolio: " + portfolio);
    }

    private int extractRequestId(String input) {
        String key = "\"request_id\"";
        int keyIndex = getKeyIndex(input, key);

        int colonIndex = input.indexOf(':', keyIndex);
        if (colonIndex == -1) {
            throw new IllegalArgumentException("Colon not found after key");
        }

        int start = colonIndex + 1;
        while (start < input.length() && !Character.isDigit(input.charAt(start))) {
            start++;
        }

        int end = start;
        while (end < input.length() && Character.isDigit(input.charAt(end))) {
            end++;
        }

        String numberStr = input.substring(start, end);
        return Integer.parseInt(numberStr);
    }

    private int getKeyIndex(String input, String key) {
        int keyIndex = 0;
        try {
            keyIndex = input.indexOf(key);
            if (keyIndex == -1) {
                throw new IllegalArgumentException("Key not found in input");
            }
        } catch (IllegalArgumentException e) {
            loggingService.log(e.getMessage());
        }
        return keyIndex;
    }

    private String extractStatus(String input) {
        String key = "\"status\"";
        int keyIndex = getKeyIndex(input, key);

        int colonIndex = input.indexOf(':', keyIndex);
        if (colonIndex == -1) {
            throw new IllegalArgumentException("Colon not found after key");
        }

        int startQuote = input.indexOf('"', colonIndex + 1);
        int endQuote = input.indexOf('"', startQuote + 1);

        if (startQuote == -1 || endQuote == -1) {
            throw new IllegalArgumentException("Status value not properly quoted");
        }

        return input.substring(startQuote + 1, endQuote);
    }

    private String parseFavoritesToJsonGrancek(List<Symbol> symbols) {
        try {
            List<Map<String, String>> favoriteList = new ArrayList<>();
            LocalDate today = LocalDate.now();
            LocalDate fiveDaysLater = today.minusDays(7);

            for (Symbol symbol : symbols) {
                Map<String, String> stockJson = new HashMap<>();
                stockJson.put("name", symbol.getName());
                stockJson.put("from", fiveDaysLater.toString()); // Od 7 dní zpátky
                stockJson.put("to", today.toString()); // Do dnes
                favoriteList.add(stockJson);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(favoriteList);
        } catch (Exception e) {
            loggingService.log("Error converting filtered favorites to JSON: " + e.getMessage());
            return "[]";
        }
    }

    private String convertJsonStringToStatusJson(String inputJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> companies = mapper.readValue(inputJson, new TypeReference<>() {
            });

            ArrayNode outputArray = mapper.createArrayNode();

            for (Map<String, Object> company : companies) {
                String companyName = (String) company.get("company_name");
                double rating = (Double) company.get("rating");

                int status = rating > tolerance ? 1 : 0;

                ObjectNode companyNode = mapper.createObjectNode();
                companyNode.put("name", companyName);
                companyNode.put("status", status);

                outputArray.add(companyNode);
            }

            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(outputArray);
        } catch (Exception e) {
            loggingService.log("Error converting JSON to status JSON: " + e.getMessage());
            return "[]";
        }
    }


}