package com.example.burza.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data model representing a stock symbol.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Symbol {
    private String symbol;
    private String name;
}
