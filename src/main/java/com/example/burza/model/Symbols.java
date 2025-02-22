package com.example.burza.model;

import lombok.Data;

import java.util.List;

@Data
public class Symbols {
    private List<Symbol> symbols; // Pole symbolů

    public List<Symbol> getSymbols() {
        return symbols;
    }
}
