package com.example.burza.controller;

import com.example.burza.model.DaysDeclineFilter;
import com.example.burza.model.FavoriteStocks;
import com.example.burza.model.Portfolio;
import com.example.burza.model.Symbol;
import com.example.burza.service.LoggingService;
import com.example.burza.service.PortfolioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RatingControllerTest {

    private PortfolioService portfolioService;
    private DaysDeclineFilter daysDeclineFilter;
    private LoggingService loggingService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        portfolioService = mock(PortfolioService.class);
        daysDeclineFilter = mock(DaysDeclineFilter.class);
        loggingService = mock(LoggingService.class);

        RatingController ratingController = new RatingController(portfolioService, daysDeclineFilter, loggingService);

        mockMvc = MockMvcBuilders.standaloneSetup(ratingController).build();
    }

    @Test
    void triggerTransaction_ShouldFilterAndCallTransaction_WhenSymbolsPassFilter() throws Exception {
        // Připrav portfolio přímo tady
        Portfolio portfolio = new Portfolio();
        FavoriteStocks favoriteStocks = new FavoriteStocks();
        favoriteStocks.getSymbols().add(new Symbol("AAPL", "Apple"));
        portfolio.setFavoriteStocks(favoriteStocks);

        when(portfolioService.getPortfolio()).thenReturn(portfolio);
        when(daysDeclineFilter.filter(anyList(), anyInt()))
                .thenReturn(List.of(new Symbol("AAPL", "Apple"))); // filtr něco vrátí

        mockMvc.perform(post("/api/rating")
                        .contentType("application/json"))
                .andExpect(status().isOk());

        verify(portfolioService, times(1)).transaction(anyList());
    }

    @Test
    void triggerTransaction_ShouldNotCallTransaction_WhenNoSymbolsPassFilter() throws Exception {
        // Připrav portfolio přímo tady
        Portfolio portfolio = new Portfolio();
        FavoriteStocks favoriteStocks = new FavoriteStocks();
        favoriteStocks.getSymbols().add(new Symbol("AAPL", "Apple"));
        portfolio.setFavoriteStocks(favoriteStocks);

        when(portfolioService.getPortfolio()).thenReturn(portfolio);
        when(daysDeclineFilter.filter(anyList(), anyInt()))
                .thenReturn(List.of()); // filtr nic nevrátí

        mockMvc.perform(post("/api/rating")
                        .contentType("application/json"))
                .andExpect(status().isOk());

        verify(portfolioService, never()).transaction(anyList());
    }
}
