package com.example.burza.service;

import com.example.burza.model.Portfolio;
import com.example.burza.model.Recommendation;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PortfolioService {
    // Import java.util.Map, com.example.burza.model.Portfolio, ...
    private final Map<String, Portfolio> userPortfolios = new HashMap<>();

    public void executeRecommendation(String userId, Recommendation recommendation) {
        // Logika: prodej/koupě
    }
}
