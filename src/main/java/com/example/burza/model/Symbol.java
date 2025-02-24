package com.example.burza.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Data model representing a stock symbol.
 */
@Data
@NoArgsConstructor  // Přidá bezparametrický konstruktor pro Jackson
@AllArgsConstructor // Přidá konstruktor se všemi argumenty
public class Symbol {
    private String symbol;
    private String name;
}
