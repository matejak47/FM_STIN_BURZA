package com.example.burza.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents the result of a trade execution.
 * Contains success status, message, executed price, total cost, and remaining balance.
 */
@Data
@AllArgsConstructor
public class TradeResult {
    private boolean success;
    private String message;
    private double executedPrice;
    private double totalCost;
    private double remainingBalance;
}