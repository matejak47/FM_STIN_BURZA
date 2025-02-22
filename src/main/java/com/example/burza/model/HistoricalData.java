package com.example.burza.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data model representing historical stock market data.
 */
@Data
@AllArgsConstructor
public class HistoricalData {
    private String date;
    private double openPrice;
    private double closePrice;
}