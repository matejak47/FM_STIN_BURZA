package com.example.burza.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FavoriteStocksTest {

    private FavoriteStocks favoriteStocks;

    @BeforeEach
    void setUp() {
        favoriteStocks = new FavoriteStocks();
    }

    @Test
    void testConstructor() {
        assertNotNull(favoriteStocks.getSymbols());
        assertTrue(favoriteStocks.getSymbols().isEmpty());
    }

    @Test
    void testAddSymbolSuccessfully() {
        favoriteStocks.addSymbol(new Symbol("AAPL", "Apple Inc."));
        favoriteStocks.addSymbol(new Symbol("GOOGL", "Google Inc."));
        assertEquals(List.of(
                new Symbol("AAPL", "Apple Inc."),
                new Symbol("GOOGL", "Google Inc.")
        ), favoriteStocks.getSymbols());
    }

    @Test
    void testAddDuplicateSymbol() {
        favoriteStocks.addSymbol(new Symbol("TSLA", "Tesla Inc."));
        favoriteStocks.addSymbol(new Symbol("TSLA", "Tesla Inc.")); // Should not allow duplicates
        assertEquals(1, favoriteStocks.getSymbols().size());
    }

    @Test
    void testAddSymbolExceedingLimit() {
        favoriteStocks.addSymbol(new Symbol("AAPL", "Apple Inc."));
        favoriteStocks.addSymbol(new Symbol("GOOGL", "Google Inc."));
        favoriteStocks.addSymbol(new Symbol("TSLA", "Tesla Inc."));
        favoriteStocks.addSymbol(new Symbol("AMZN", "Amazon Inc."));
        favoriteStocks.addSymbol(new Symbol("MSFT", "Microsoft Inc."));
        // 6. pokus už přesáhne limit, test si můžeš přidat
        boolean added = favoriteStocks.addSymbol(new Symbol("NFLX", "Netflix Inc."));
        assertFalse(added); // mělo by vrátit false
    }


    @Test
    void testRemoveSymbolSuccessfully() {
        favoriteStocks.addSymbol(new Symbol("AAPL", "Apple Inc."));
        favoriteStocks.addSymbol(new Symbol("TSLA", "Tesla Inc."));

        assertTrue(favoriteStocks.removeSymbol("AAPL"));
        assertFalse(favoriteStocks.getSymbols().stream()
                .anyMatch(s -> s.getSymbol().equals("AAPL")));
        assertEquals(1, favoriteStocks.getSymbols().size());
    }


    @Test
    void testRemoveNonExistingSymbol() {
        assertFalse(favoriteStocks.removeSymbol("GOOGL")); // Not in list
    }

    @Test
    void testRemoveFromEmptyList() {
        assertFalse(favoriteStocks.removeSymbol("AAPL")); // Should return false since list is empty
    }
}
