package com.example.burza.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StockResponse {
    private String name;
    private long date;
    private int rating;
    private Integer sell;
}