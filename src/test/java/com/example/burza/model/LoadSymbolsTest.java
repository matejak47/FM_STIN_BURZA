package com.example.burza.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoadSymbolsTest {

    @InjectMocks
    private LoadSymbols loadSymbols;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private InputStream inputStream;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadSymbols() throws IOException {
        // Mockovaná data
        Symbol symbol1 = new Symbol("AAPL", "Apple Inc.");
        Symbol symbol2 = new Symbol("GOOGL", "Alphabet Inc.");
        Symbols mockSymbols = new Symbols();
        mockSymbols.setSymbols(Arrays.asList(symbol1, symbol2));


        // Mockování metody ObjectMapper.readValue()
        when(objectMapper.readValue(any(InputStream.class), eq(Symbols.class))).thenReturn(mockSymbols);

        // Spuštění metody
        List<Symbol> result = loadSymbols.LoadSymbols();

        // Ověření výsledků
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("AAPL", result.get(0).getSymbol());
        assertEquals("Apple Inc.", result.get(0).getName());
        assertEquals("GOOGL", result.get(1).getSymbol());
        assertEquals("Alphabet Inc.", result.get(1).getName());

        // Ověření volání metod
        verify(objectMapper).readValue(any(InputStream.class), eq(Symbols.class));
    }
}
