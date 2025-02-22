package com.example.burza.model;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents user's favorite stocks.
 * Maintains a list of favorite stock symbols with a maximum limit.
 */
@Data
public class FavoriteStocks {
    private static final int MAX_FAVORITES = 5;
    private List<String> symbols;

    public FavoriteStocks() {
        this.symbols = new ArrayList<>();
    }

    /**
     * Adds a symbol to favorites if limit not reached.
     * @param symbol Stock symbol to add
     * @return true if added successfully, false if limit reached
     */
    public boolean addSymbol(String symbol) {
        if (symbols.size() >= MAX_FAVORITES) {
            return false;
        }
        if (!symbols.contains(symbol)) {
            symbols.add(symbol);
            return true;
        }
        return false;
    }

    /**
     * Removes a symbol from favorites.
     * @param symbol Stock symbol to remove
     * @return true if removed, false if not found
     */
    public boolean removeSymbol(String symbol) {
        return symbols.remove(symbol);
    }
}

