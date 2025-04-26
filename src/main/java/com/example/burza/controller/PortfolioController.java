package com.example.burza.controller;

import com.example.burza.model.Symbol;
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
     *
     * @return List of favorite stock objects
     */
    @GetMapping("/favorites")
    public List<Symbol> getFavoriteStocks() {
        return portfolioService.getPortfolio().getFavoriteStocks().getSymbols();
    }

    /**
     * Adds a stock to favorites.
     *
     * @param symbol Stock symbol object to add
     * @return true if added successfully, false if limit reached
     */
    @PostMapping("/favorites")
    public boolean addFavoriteStock(@RequestBody Symbol symbol) {
        return portfolioService.getPortfolio().getFavoriteStocks().addSymbol(symbol);
    }

    /**
     * Removes a stock from favorites.
     *
     * @param symbol Stock symbol string to remove
     * @return true if removed successfully
     */
    @DeleteMapping("/favorites/{symbol}")
    public boolean removeFavoriteStock(@PathVariable String symbol) {
        return portfolioService.getPortfolio().getFavoriteStocks().removeSymbol(symbol);
    }


}
