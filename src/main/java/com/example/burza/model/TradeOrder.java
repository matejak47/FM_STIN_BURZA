package com.example.burza.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Represents a trade order.
 * Contains stock symbol, order type (buy/sell), and quantity.
 */
@Service
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeOrder {
    private String symbol;
    private OrderType orderType;
    private int quantity;

    public enum OrderType {
        BUY, SELL
    }
}