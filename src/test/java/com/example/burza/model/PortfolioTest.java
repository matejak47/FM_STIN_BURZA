package com.example.burza.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PortfolioTest {

    private Portfolio portfolio;

    @BeforeEach
    public void setUp() {
        portfolio = new Portfolio(); // Initialize Portfolio before each test
    }

    @Test
    public void testInitialBalance() {
        // Test the initial balance is set to 10000.0
        assertEquals(10000.0, portfolio.getBalance(), "Balance should be 10000.0");
    }

    @Test
    public void testInitialHoldings() {
        // Test that holdings are initialized as an empty map
        Map<String, Integer> holdings = portfolio.getHoldings();
        assertNotNull(holdings, "Holdings should not be null");
        assertTrue(holdings.isEmpty(), "Holdings map should be empty initially");
    }

    @Test
    public void testAddStockToPortfolio() {
        // Test adding a stock to the holdings
        portfolio.getHoldings().put("AAPL", 50); // Adding 50 shares of AAPL
        assertEquals(50, portfolio.getHoldings().get("AAPL"), "Should have 50 shares of AAPL");
    }

    @Test
    public void testFavoriteStocksInitialization() {
        // Assuming that FavoriteStocks has a default constructor and is not null
        assertNotNull(portfolio.getFavoriteStocks(), "FavoriteStocks should not be null");
    }

    // You can also add tests for FavoriteStocks functionality if needed

}
