package com.example.burza.model;

import java.util.List;

public interface Filter {
    List<Symbol> filter(List<Symbol> symbols, int numberOfDays);
}
