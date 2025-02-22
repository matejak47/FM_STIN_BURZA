package com.example.burza.controller;

import com.example.burza.service.PortfolioService;
import com.example.burza.service.StockService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {
    private final PortfolioService portfolioService;
    private final StockService stockService;

    public PortfolioController(PortfolioService portfolioService, StockService stockService) {
        this.portfolioService = portfolioService;
        this.stockService = stockService;
    }

    /**
     * Retrieves list of favorite stocks.
     * @return List of favorite stock symbols
     */
    @GetMapping("/favorites")
    public List<String> getFavoriteStocks() {
        return portfolioService.getPortfolio().getFavoriteStocks().getSymbols();
    }

    @GetMapping("/favorites/decline")
    public List<String> getFavoriteStocksDecline(@RequestParam int days) {
        List<String> symbols =  portfolioService.getPortfolio().getFavoriteStocks().getSymbols();
        return stockService.getSymbolsWithDecline(symbols,days);

    }

    @GetMapping("/favorites/increase")
    public List<String> getFavoriteStocksIncrease(@RequestParam int days) {
        List<String> symbols =  portfolioService.getPortfolio().getFavoriteStocks().getSymbols();
        return stockService.getSymbolsWithIncrease(symbols,days);
    }

    /**
     * Adds a stock to favorites.
     * @param symbol Stock symbol to add
     * @return true if added successfully, false if limit reached
     */
    @PostMapping("/favorites/{symbol}")
    public boolean addFavoriteStock(@PathVariable String symbol) {
        return portfolioService.getPortfolio().getFavoriteStocks().addSymbol(symbol);
    }

    /**
     * Removes a stock from favorites.
     * @param symbol Stock symbol to remove
     * @return true if removed successfully
     */
    @DeleteMapping("/favorites/{symbol}")
    public boolean removeFavoriteStock(@PathVariable String symbol) {
        return portfolioService.getPortfolio().getFavoriteStocks().removeSymbol(symbol);
    }

}

