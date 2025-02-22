package com.example.burza.model;

import lombok.Data;

/**
 * Represents a trade order.
 * Contains stock symbol, order type (buy/sell), and quantity.
 */
@Data
public class TradeOrder {
    private String symbol;
    private OrderType orderType;
    private int quantity;

    public enum OrderType {
        BUY, SELL
    }
}