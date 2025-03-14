package com.example.burza.controller;

import com.example.burza.model.StockResponse;
import com.example.burza.service.PortfolioService;
import com.example.burza.service.StockService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {
    private static final String EXTERNAL_API_URL = "https://web.postman.co/workspace/My-Workspace~fcdea791-0555-4510-8cff-6edfde81a02a/mock/35d98169-e4da-4367-bdbc-506639f64d28";
    private final RestTemplate restTemplate = new RestTemplate();
    private final PortfolioService portfolioService;
    private final StockService stockService;

    public PortfolioController(PortfolioService portfolioService, StockService stockService) {
        this.portfolioService = portfolioService;
        this.stockService = stockService;
    }

    /**
     * Retrieves list of favorite stocks.
     *
     * @return List of favorite stock symbols
     */
    @GetMapping("/favorites")
    public List<String> getFavoriteStocks() {
        return portfolioService.getPortfolio().getFavoriteStocks().getSymbols();
    }


    @GetMapping("/favorites/decline")
    public List<StockResponse> getFavoriteStocksDecline(@RequestParam int days) {
        List<String> symbols = portfolioService.getPortfolio().getFavoriteStocks().getSymbols();
        List<String> output = stockService.getSymbolsWithDecline(symbols, days);
        return portfolioService.parseToJson(output);

    }

    @GetMapping("/favorites/increase")
    public List<StockResponse> getFavoriteStocksIncrease(@RequestParam int days) {
        List<String> symbols = portfolioService.getPortfolio().getFavoriteStocks().getSymbols();
        List<String> output = stockService.getSymbolsWithIncrease(symbols, days);
        return portfolioService.parseToJson(output);
    }

    @PostMapping("/send-to-external")
    public ResponseEntity<String> sendStocksToExternalApi(@RequestBody List<StockResponse> requestData) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<List<StockResponse>> requestEntity = new HttpEntity<>(requestData, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(EXTERNAL_API_URL, requestEntity, String.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send data to external API: External API error");
        }
    }

    /**
     * Adds a stock to favorites.
     *
     * @param symbol Stock symbol to add
     * @return true if added successfully, false if limit reached
     */
    @PostMapping("/favorites/{symbol}")
    public boolean addFavoriteStock(@PathVariable String symbol) {
        return portfolioService.getPortfolio().getFavoriteStocks().addSymbol(symbol);
    }

    /**
     * Removes a stock from favorites.
     *
     * @param symbol Stock symbol to remove
     * @return true if removed successfully
     */
    @DeleteMapping("/favorites/{symbol}")
    public boolean removeFavoriteStock(@PathVariable String symbol) {
        return portfolioService.getPortfolio().getFavoriteStocks().removeSymbol(symbol);
    }

}

