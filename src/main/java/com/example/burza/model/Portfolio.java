package com.example.burza.model;

import lombok.Data;
import java.util.Map;

/**
 * Data model representing a user's portfolio.
 */
import lombok.Data;
import java.util.HashMap;
import java.util.Map;

/**
 * Data model representing a user's portfolio.
 * Contains stock holdings and available balance.
 */
@Data
public class Portfolio {
    private Map<String, Integer> holdings;
    private double balance;
    private FavoriteStocks favoriteStocks;

    public Portfolio() {
        this.holdings = new HashMap<>();
        this.balance = 10000.0;
        this.favoriteStocks = new FavoriteStocks();
    }
}
