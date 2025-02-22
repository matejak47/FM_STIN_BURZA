package com.example.burza.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data model representing daily stock market data.
 */
@Data
@AllArgsConstructor
public class DailyData {

    private String date;
    private double open;
    private double high;
    private double low;
    private double close;
    private long volume;

}
