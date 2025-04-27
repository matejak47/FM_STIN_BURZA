package com.example.burza.controller;

import com.example.burza.model.Portfolio;
import com.example.burza.model.TradeOrder;
import com.example.burza.model.TradeResult;
import com.example.burza.service.PortfolioService;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling trade operations.
 * Provides endpoints to execute trades and retrieve portfolio details.
 */
@RestController
@RequestMapping("/api/trade")
public class TradeController {
    private final PortfolioService portfolioService;

    /**
     * Constructor to initialize PortfolioService.
     *
     * @param portfolioService the service handling portfolio operations
     */
    public TradeController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    /**
     * Executes a trade order.
     *
     * @param order the trade order containing symbol, type, and quantity
     * @return the result of the trade execution
     */
    @PostMapping("/execute")
    public TradeResult executeOrder(@RequestBody TradeOrder order) {
        return portfolioService.executeTrade(order);
    }


    /**
     * Retrieves the current portfolio.
     *
     * @return the user's portfolio with holdings and balance
     */
    @GetMapping("/portfolio")
    public Portfolio getPortfolio() {
        return portfolioService.getPortfolio();
    }
}