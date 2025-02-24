package com.example.burza.controller;

import com.example.burza.model.FavoriteStocks;
import com.example.burza.model.Portfolio;
import com.example.burza.service.PortfolioService;
import com.example.burza.service.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PortfolioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PortfolioService portfolioService;

    @Mock
    private StockService stockService;

    @Mock
    private Portfolio portfolio;
    @Mock
    private FavoriteStocks favoriteStocks;

    @InjectMocks
    private PortfolioController portfolioController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(portfolioController).build();
    }

    @Test
    void testGetFavoriteStocks() throws Exception {
        when(portfolioService.getPortfolio()).thenReturn(portfolio);
        when(portfolio.getFavoriteStocks()).thenReturn(favoriteStocks);
        when(favoriteStocks.getSymbols()).thenReturn(List.of("AAPL", "TSLA"));

        mockMvc.perform(get("/api/portfolio/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0]").value("AAPL"));
    }

    @Test
    void testAddFavoriteStock() throws Exception {
        when(portfolioService.getPortfolio()).thenReturn(portfolio);
        when(portfolio.getFavoriteStocks()).thenReturn(favoriteStocks);
        when(favoriteStocks.addSymbol("AAPL")).thenReturn(true);

        mockMvc.perform(post("/api/portfolio/favorites/AAPL"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testRemoveFavoriteStock() throws Exception {
        when(portfolioService.getPortfolio()).thenReturn(portfolio);
        when(portfolio.getFavoriteStocks()).thenReturn(favoriteStocks);
        when(favoriteStocks.removeSymbol("AAPL")).thenReturn(true);

        mockMvc.perform(delete("/api/portfolio/favorites/AAPL"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
    @Test
    void testGetFavoriteStocksDecline() throws Exception {
        when(portfolioService.getPortfolio()).thenReturn(portfolio);
        when(portfolio.getFavoriteStocks()).thenReturn(favoriteStocks);
        when(favoriteStocks.getSymbols()).thenReturn(List.of("AAPL", "TSLA"));
        when(stockService.getSymbolsWithDecline(List.of("AAPL", "TSLA"), 5)).thenReturn(List.of("AAPL"));

        mockMvc.perform(get("/api/portfolio/favorites/decline?days=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0]").value("AAPL"));
    }

    @Test
    void testGetFavoriteStocksIncrease() throws Exception {
        when(portfolioService.getPortfolio()).thenReturn(portfolio);
        when(portfolio.getFavoriteStocks()).thenReturn(favoriteStocks);
        when(favoriteStocks.getSymbols()).thenReturn(List.of("AAPL", "TSLA"));
        when(stockService.getSymbolsWithIncrease(List.of("AAPL", "TSLA"), 5)).thenReturn(List.of("TSLA"));

        mockMvc.perform(get("/api/portfolio/favorites/increase?days=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0]").value("TSLA"));
    }
}
