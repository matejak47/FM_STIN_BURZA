package com.example.burza.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoadSymbolsTest {

    @InjectMocks
    private LoadSymbols loadSymbols;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ClassPathResource resource;

    @Mock
    private InputStream inputStream;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadSymbols_Success() throws Exception {
        Symbols mockSymbols = new Symbols();
        mockSymbols.setSymbols(List.of(
                new Symbol("AAPL", "Apple Inc."),
                new Symbol("TSLA", "Tesla Inc.")
        ));

        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenReturn(inputStream);
        when(objectMapper.readValue(inputStream, Symbols.class)).thenReturn(mockSymbols);

        List<Symbol> result = loadSymbols.LoadSymbols();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("AAPL", result.get(0).getSymbol());
    }

    @Test
    void testLoadSymbols_FileNotFound() throws Exception {
        when(resource.exists()).thenReturn(false);

        Exception exception = assertThrows(IOException.class, () -> loadSymbols.LoadSymbols());
        assertTrue(exception.getMessage().contains("data/symbols.json"));
    }

    @Test
    void testLoadSymbols_ReadError() throws Exception {
        when(resource.exists()).thenReturn(true);
        when(resource.getInputStream()).thenThrow(new IOException("Read error"));

        Exception exception = assertThrows(IOException.class, () -> loadSymbols.LoadSymbols());
        assertTrue(exception.getMessage().contains("Read error"));
    }
}
