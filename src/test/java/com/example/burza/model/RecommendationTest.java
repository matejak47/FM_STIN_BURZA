package com.example.burza.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RecommendationTest {

    private Recommendation recommendation;

    @BeforeEach
    public void setUp() {
        recommendation = new Recommendation(); // Initialize Recommendation before each test
    }

    @Test
    public void testGettersAndSetters() {
        // Set the properties
        recommendation.setSymbol("AAPL");
        recommendation.setShouldBuy(true);
        recommendation.setShouldSell(false);

        // Test the getters
        assertEquals("AAPL", recommendation.getSymbol(), "Symbol should be 'AAPL'");
        assertTrue(recommendation.isShouldBuy(), "Should buy should be true");
        assertFalse(recommendation.isShouldSell(), "Should sell should be false");
    }

    @Test
    public void testDefaultValues() {
        // Test default values for an uninitialized Recommendation object
        assertNull(recommendation.getSymbol(), "Symbol should be null by default");
        assertFalse(recommendation.isShouldBuy(), "Should buy should be false by default");
        assertFalse(recommendation.isShouldSell(), "Should sell should be false by default");
    }
}
