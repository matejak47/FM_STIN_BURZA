package com.example.burza.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class HistoricalData {
    private LocalDate date;
    private double openPrice;
    private double closePrice;
}
