package com.example.burza.controller;

import com.example.burza.model.*;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BurzaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BurzaService burzaService;

    @Mock
    private StockService stockService;

    @Mock
    private LoadSymbols loadSymbols;

    @InjectMocks
    private BurzaController burzaController;

    private List<HistoricalData> historicalData;
    private List<DailyData> dailyData;
    private List<Symbol> symbols;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(burzaController).build();

        historicalData = Arrays.asList(
                new HistoricalData("2024-01-01", 150.0, 145.0),
                new HistoricalData("2024-01-02", 148.0, 140.0)
        );

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
    void testFilterDataDown() throws Exception {
        when(burzaService.filterDataDown(historicalData)).thenReturn(historicalData);

        mockMvc.perform(post("/api/burza/filterdown")
                        .contentType("application/json")
                        .content("[{\"date\":\"2024-01-01\",\"openPrice\":150.0,\"closePrice\":145.0}]"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetHistoricalData() throws Exception {
        when(burzaService.fetchHistoricalData("IBM")).thenReturn(historicalData);

        mockMvc.perform(get("/api/burza/historical?symbol=IBM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].date").value("2024-01-01"))
                .andExpect(jsonPath("$[0].openPrice").value(150.0))
                .andExpect(jsonPath("$[0].closePrice").value(145.0));
    }

    @Test
    void testGetFilteredHistoricalData() throws Exception {
        when(burzaService.fetchHistoricalData("IBM")).thenReturn(historicalData);
        when(burzaService.filterDataDown(historicalData)).thenReturn(historicalData);

        mockMvc.perform(get("/api/burza/historical/filterDown?symbol=IBM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetFilteredUpData() throws Exception {
        when(burzaService.fetchHistoricalData("IBM")).thenReturn(historicalData);
        when(burzaService.filterDataUp(historicalData)).thenReturn(historicalData);

        mockMvc.perform(get("/api/burza/historical/filterUp?symbol=IBM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
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
    void testGetRecentData() throws Exception {
        when(stockService.fetchDailyTimeSeries("IBM")).thenReturn(dailyData);
        when(stockService.fetchDailyDataByTime(dailyData, "2024-01-02")).thenReturn(dailyData);

        mockMvc.perform(get("/api/burza/daily/date?symbol=IBM&date=2024-01-02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetAllSymbols() throws Exception {
        when(loadSymbols.LoadSymbols()).thenReturn(symbols);

        mockMvc.perform(get("/api/burza/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].symbol").value("AAPL"));
    }

    @Test
    void testGetAllSymbols_Exception() throws Exception {
        doThrow(new IOException("File not found")).when(loadSymbols).LoadSymbols();

        mockMvc.perform(get("/api/burza/all"))
                .andExpect(status().isInternalServerError());
    }
}
