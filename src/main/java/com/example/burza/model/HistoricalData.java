package com.example.burza.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor // Automaticky vytvoří konstruktor se všemi parametry
public class HistoricalData {
    private String date; // Původně LocalDate, ale JSON používá String
    private double openPrice;
    private double closePrice;
}