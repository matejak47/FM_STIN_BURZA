package com.example.burza.controller;

import com.example.burza.model.DailyData;
import com.example.burza.model.Symbol;
import com.example.burza.model.SymbolLoading;
import com.example.burza.service.BurzaService;
import com.example.burza.service.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BurzaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BurzaService burzaService;

    @Mock
    private StockService stockService;

    @Mock
    private SymbolLoading symbolLoading;

    @InjectMocks
    private BurzaController burzaController;

    private List<DailyData> dailyData;
    private List<Symbol> symbols;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(burzaController).build();

        dailyData = Arrays.asList(
                new DailyData("2024-01-01", 150.0, 155.0, 149.0, 153.0, 50000),
                new DailyData("2024-01-02", 152.0, 156.0, 150.0, 154.0, 48000)
        );

        symbols = Arrays.asList(
                new Symbol("AAPL", "Apple Inc."),
                new Symbol("IBM", "International Business Machines Corp.")
        );
    }

    @Test
    void testGetDailyData() throws Exception {
        when(stockService.fetchDailyTimeSeries("IBM")).thenReturn(dailyData);

        mockMvc.perform(get("/api/burza/daily?symbol=IBM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].date").value("2024-01-01"))
                .andExpect(jsonPath("$[0].close").value(153.0));
    }

    @Test
    void testGetAllSymbols() throws Exception {
        when(symbolLoading.LoadSymbols()).thenReturn(symbols);

        mockMvc.perform(get("/api/burza/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].symbol").value("AAPL"));
    }

    @Test
    void testGetAllSymbols_Exception() throws Exception {
        doThrow(new IOException("File not found")).when(symbolLoading).LoadSymbols();

        mockMvc.perform(get("/api/burza/all"))
                .andExpect(status().isInternalServerError());
    }
}
