package com.example.burza.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Data model representing historical stock market data.
 */
@Service
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoricalData {
    private String date;
    private double openPrice;
    private double closePrice;
}