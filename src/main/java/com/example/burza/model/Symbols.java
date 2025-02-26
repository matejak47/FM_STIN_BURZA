package com.example.burza.model;

import lombok.Data;
import lombok.Getter;

import java.util.List;

/**
 * Data model representing a collection of stock symbols.
 */
@Getter
@Data
public class Symbols {
    private List<Symbol> symbols; // Pole symbolů

}
