package com.example.burza.controller;

import com.example.burza.model.FavoriteStocks;
import com.example.burza.model.Portfolio;
import com.example.burza.model.StockResponse;
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
import org.springframework.web.client.RestTemplate;

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

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PortfolioController portfolioController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(portfolioController).build();
    }

    @Test
    void testSendStocksToExternalApi_Success() throws Exception {
        // Sample JSON request body
        String requestBody = """
                [
                    {"name": "Microsoft", "date": 1710458392000, "rating": -10, "sale": 1},
                    {"name": "Google", "date": 1710458392000, "rating": 10, "sale": 0}
                ]
                """;

        // Mock successful response from external API
        when(restTemplate.postForEntity(any(String.class), any(), any()))
                .thenReturn(org.springframework.http.ResponseEntity.ok("Mock API Success"));

        // Perform the request
        mockMvc.perform(post("/api/portfolio/send-to-external")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk()); // Expect HTTP 200
    }

    @Test
    void testSendStocksToExternalApi_Failure() throws Exception {
        // Sample JSON request body
        String requestBody = """
                [
                    {"name": "Apple", "date": 1710458392000, "rating": 0, "sale": null}
                ]
                """;

        // Mock failure response from external API
        when(restTemplate.postForEntity(any(String.class), any(), any()))
                .thenThrow(new RuntimeException("External API error"));

        // Perform the request
        mockMvc.perform(post("/api/portfolio/send-to-external")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isInternalServerError()) // Expect HTTP 500
                .andExpect(content().string("Failed to send data to external API: External API error"));
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
        List<String> favoriteSymbols = List.of("AAPL", "TSLA");
        List<String> decliningSymbols = List.of("AAPL");
        List<StockResponse> responseList = List.of(new StockResponse("AAPL", 1710458392000L, -10, 1));

        when(portfolioService.getPortfolio()).thenReturn(portfolio);
        when(portfolio.getFavoriteStocks()).thenReturn(favoriteStocks);
        when(favoriteStocks.getSymbols()).thenReturn(favoriteSymbols);
        when(stockService.getSymbolsWithDecline(favoriteSymbols, 5)).thenReturn(decliningSymbols);
        when(portfolioService.parseToJson(decliningSymbols)).thenReturn(responseList);

        mockMvc.perform(get("/api/portfolio/favorites/decline?days=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("AAPL"))
                .andExpect(jsonPath("$[0].rating").value(-10))
                .andExpect(jsonPath("$[0].sale").value(1));
    }

    @Test
    void testGetFavoriteStocksIncrease() throws Exception {
        List<String> favoriteSymbols = List.of("AAPL", "TSLA");
        List<String> increasingSymbols = List.of("TSLA");
        List<StockResponse> responseList = List.of(new StockResponse("TSLA", 1710458392000L, 10, 0));

        when(portfolioService.getPortfolio()).thenReturn(portfolio);
        when(portfolio.getFavoriteStocks()).thenReturn(favoriteStocks);
        when(favoriteStocks.getSymbols()).thenReturn(favoriteSymbols);
        when(stockService.getSymbolsWithIncrease(favoriteSymbols, 5)).thenReturn(increasingSymbols);
        when(portfolioService.parseToJson(increasingSymbols)).thenReturn(responseList);

        mockMvc.perform(get("/api/portfolio/favorites/increase?days=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("TSLA"))
                .andExpect(jsonPath("$[0].rating").value(10))
                .andExpect(jsonPath("$[0].sale").value(0));
    }

}
