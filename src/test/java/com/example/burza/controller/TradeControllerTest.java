package com.example.burza.controller;

import com.example.burza.model.Portfolio;
import com.example.burza.model.TradeResult;
import com.example.burza.service.PortfolioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TradeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PortfolioService portfolioService;

    @InjectMocks
    private TradeController tradeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(tradeController).build();
    }

    @Test
    void testExecuteOrder() throws Exception {
        TradeResult tradeResult = new TradeResult(true, "Trade successful", 100, 200, 5000);

        when(portfolioService.executeTrade(any())).thenReturn(tradeResult);

        String tradeOrderJson = "{\"symbol\":\"AAPL\",\"orderType\":\"BUY\",\"quantity\":2}";

        mockMvc.perform(post("/api/trade/execute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tradeOrderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Trade successful"));
    }

    @Test
    void testGetPortfolio() throws Exception {
        Portfolio portfolio = new Portfolio();

        when(portfolioService.getPortfolio()).thenReturn(portfolio);

        mockMvc.perform(get("/api/trade/portfolio"))
                .andExpect(status().isOk());
    }
}

