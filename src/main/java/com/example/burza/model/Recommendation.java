package com.example.burza.model;

import lombok.Data;

/**
 * Data model representing a trading recommendation.
 */
@Data
public class Recommendation {
    private String symbol;
    private boolean shouldBuy;
    private boolean shouldSell;
}
