package com.example.burza.model;

import lombok.Data;

import java.util.List;

/**
 * Data model representing a collection of stock symbols.
 */
@Data
public class Symbols {
    private List<Symbol> symbols; // Pole symbolů

    public List<Symbol> getSymbols() {
        return symbols;
    }
}
