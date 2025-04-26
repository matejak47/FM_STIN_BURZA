package com.example.burza.controller;

import com.example.burza.model.StockResponse;
import com.example.burza.model.Symbol;
import com.example.burza.service.PortfolioService;
import com.example.burza.service.StockService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {
    private static final String EXTERNAL_API_URL = "https://6508cb37-03fb-4cac-bf4b-eacf87eebb00.mock.pstmn.io";
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

    @GetMapping("/favorites/decline")
    public List<StockResponse> getFavoriteStocksDecline(@RequestParam int days) {
        List<String> symbols = portfolioService.getPortfolio().getFavoriteStocks().getSymbols()
                .stream().map(Symbol::getSymbol).toList();
        List<String> output = stockService.getSymbolsWithDecline(symbols, days);
        return portfolioService.parseToJson(output);
    }

    @GetMapping("/favorites/increase")
    public List<StockResponse> getFavoriteStocksIncrease(@RequestParam int days) {
        List<String> symbols = portfolioService.getPortfolio().getFavoriteStocks().getSymbols()
                .stream().map(Symbol::getSymbol).toList();
        List<String> output = stockService.getSymbolsWithIncrease(symbols, days);
        return portfolioService.parseToJson(output);
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
