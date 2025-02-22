package com.example.burza.controller;

import com.example.burza.service.PortfolioService;
import org.springframework.web.bind.annotation.*;

import com.example.burza.model.Recommendation;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @PostMapping("/execute")
    public void executeRecommendation(
            @RequestParam String userId,
            @RequestBody Recommendation recommendation
    ) {
        portfolioService.executeRecommendation(userId, recommendation);
    }
}
