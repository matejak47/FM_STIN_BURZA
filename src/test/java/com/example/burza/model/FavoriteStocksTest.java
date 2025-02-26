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
        assertTrue(favoriteStocks.addSymbol("AAPL"));
        assertTrue(favoriteStocks.addSymbol("GOOGL"));
        assertEquals(List.of("AAPL", "GOOGL"), favoriteStocks.getSymbols());
    }

    @Test
    void testAddDuplicateSymbol() {
        assertTrue(favoriteStocks.addSymbol("TSLA"));
        assertFalse(favoriteStocks.addSymbol("TSLA")); // Should not allow duplicates
        assertEquals(1, favoriteStocks.getSymbols().size());
    }

    @Test
    void testAddSymbolExceedingLimit() {
        assertTrue(favoriteStocks.addSymbol("AAPL"));
        assertTrue(favoriteStocks.addSymbol("GOOGL"));
        assertTrue(favoriteStocks.addSymbol("TSLA"));
        assertTrue(favoriteStocks.addSymbol("AMZN"));
        assertTrue(favoriteStocks.addSymbol("MSFT"));

        assertFalse(favoriteStocks.addSymbol("NFLX")); // Should fail, limit is 5
        assertEquals(5, favoriteStocks.getSymbols().size());
    }

    @Test
    void testRemoveSymbolSuccessfully() {
        favoriteStocks.addSymbol("AAPL");
        favoriteStocks.addSymbol("TSLA");

        assertTrue(favoriteStocks.removeSymbol("AAPL"));
        assertFalse(favoriteStocks.getSymbols().contains("AAPL"));
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
