package com.example.burza.controller;

import com.example.burza.model.FavoriteStocks;
import com.example.burza.model.Portfolio;
import com.example.burza.model.Symbol;
import com.example.burza.service.PortfolioService;
import com.example.burza.service.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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
        when(favoriteStocks.getSymbols()).thenReturn(List.of(
                new Symbol("AAPL", "Apple Inc."),
                new Symbol("TSLA", "Tesla Inc.")
        ));

        mockMvc.perform(get("/api/portfolio/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].symbol").value("AAPL"))
                .andExpect(jsonPath("$[0].name").value("Apple Inc."));
    }

    @Test
    void testAddFavoriteStock() throws Exception {
        when(portfolioService.getPortfolio()).thenReturn(portfolio);
        when(portfolio.getFavoriteStocks()).thenReturn(favoriteStocks);
        when(favoriteStocks.addSymbol(any(Symbol.class))).thenReturn(true);

        String requestBody = """
                {
                    "symbol": "AAPL",
                    "name": "Apple Inc."
                }
                """;

        mockMvc.perform(post("/api/portfolio/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
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
}
