package com.example.burza.controller;

import com.example.burza.service.PortfolioService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rating")
public class RatingController {
    private final PortfolioService portfolioService;

    public RatingController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @PostMapping()
    public void triggerTransaction() throws InterruptedException {
        portfolioService.transaction();
    }
}
