package com.example.burza.controller;

import com.example.burza.model.DaysDeclineFilter;
import com.example.burza.model.Symbol;
import com.example.burza.service.LoggingService;
import com.example.burza.service.PortfolioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rating")
public class RatingController {
    private final PortfolioService portfolioService;
    private final DaysDeclineFilter lastThreeDaysDeclineFilter;
    private final LoggingService loggingService;
    @Value("${Days1:1}")
    private int days1;
    @Value("${Days2:5}")
    private int days2;

    public RatingController(PortfolioService portfolioService, DaysDeclineFilter lastThreeDaysDeclineFilter, LoggingService loggingService) {
        this.portfolioService = portfolioService;
        this.lastThreeDaysDeclineFilter = lastThreeDaysDeclineFilter;
        this.loggingService = loggingService;
    }

    @PostMapping
    public List<Symbol> triggerTransaction() throws InterruptedException {
        List<Symbol> favoriteSymbols = portfolioService.getPortfolio().getFavoriteStocks().getSymbols();
        List<Symbol> filteredSymbols = lastThreeDaysDeclineFilter.filter(favoriteSymbols, days1);

        if (!filteredSymbols.isEmpty()) {
            portfolioService.transaction(filteredSymbols);
        } else {
            loggingService.log("Žádné akcie neprošly filtrem, transakce se nevolá.");
        }

        return filteredSymbols;
    }

}

