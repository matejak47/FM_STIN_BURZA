package com.example.burza.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SymbolsTest {

    @Test
    void testGetSymbols() {
        List<Symbol> expectedSymbols = Arrays.asList(
                new Symbol("AAPL", "Apple Inc."),
                new Symbol("GOOGL", "Alphabet Inc."),
                new Symbol("MSFT", "Microsoft Corp.")
        );
        Symbols symbols = new Symbols();
        symbols.setSymbols(expectedSymbols);

        List<Symbol> actualSymbols = symbols.getSymbols();

        assertNotNull(actualSymbols, "Symbols list should not be null");
        assertEquals(expectedSymbols, actualSymbols, "Symbols list should match the expected list");
    }
}
