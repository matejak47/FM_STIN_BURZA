package com.example.burza.model;

import lombok.Data;

@Data
public class Recommendation {
    private String symbol;
    private boolean shouldBuy;
    private boolean shouldSell;
}
