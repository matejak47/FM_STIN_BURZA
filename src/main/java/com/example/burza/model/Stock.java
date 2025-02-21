package com.example.burza.model;
import lombok.Data;

@Data
public class Stock {
    private String symbol;
    private String name;
    private double currentPrice;
}
