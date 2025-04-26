package com.example.burza.service;

import com.example.burza.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final RestTemplate restTemplate;
    boolean testMode = false;

    /**
     * Constructor initializing portfolio and stock service.
     *
     * @param stockService service for fetching stock data
     */
    @Autowired
    public PortfolioService(StockService stockService, RestTemplate restTemplate) {
        this.stockService = stockService;
        this.portfolio = new Portfolio();
        this.restTemplate = restTemplate;
    }

    public int sendDataToGrancek() {
        String SendJson = parseFavoritesToJsonGrancek(portfolio.getFavoriteStocks());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(SendJson, headers);
        String url = newsUrl + "/submit";

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
        );

        return extractRequestId(response.getBody());
    }


    public String receiveDataFromGrancek(int request_id) throws InterruptedException {
        String url = newsUrl + "/output/" + request_id + "/status";
        System.out.println(url);
        boolean running = true;
        while (running) {
            String ReceiveJson = restTemplate.getForObject(url, String.class);
            System.out.println(ReceiveJson);
            if (extractStatus(ReceiveJson).equals("done")) {
                System.out.println("Done");
                running = false;
                continue;
            }

            if (!testMode) {
                Thread.sleep(2000);
            }
        }
        String ReceiveJson = restTemplate.getForObject(newsUrl + "/output/" + request_id, String.class);
        System.out.println("ReceiveJson: " + ReceiveJson);
        return ReceiveJson;
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

    private static int extractRequestId(String input) {
        String key = "\"request_id\"";
        int keyIndex = input.indexOf(key);
        if (keyIndex == -1) {
            throw new IllegalArgumentException("Key not found");
        }

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

    private static String extractStatus(String input) {
        String key = "\"status\"";
        int keyIndex = input.indexOf(key);
        if (keyIndex == -1) {
            throw new IllegalArgumentException("Key not found");
        }

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

    private String parseFavoritesToJsonGrancek(FavoriteStocks favourites) {
        try {
            List<Map<String, String>> favoriteList = new ArrayList<>();
            LocalDate today = LocalDate.now();
            LocalDate fiveDaysLater = today.plusDays(5);

            for (Symbol symbol : favourites.getSymbols()) {
                Map<String, String> stockJson = new HashMap<>();
                stockJson.put("name", symbol.getName());
                stockJson.put("from", today.toString());
                stockJson.put("to", fiveDaysLater.toString());
                favoriteList.add(stockJson);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(favoriteList);
        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }

}