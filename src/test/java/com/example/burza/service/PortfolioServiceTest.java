package com.example.burza.service;

import com.example.burza.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PortfolioServiceTest {

    @Mock
    private StockService stockService;

    @InjectMocks
    private PortfolioService portfolioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecuteBuy() {
        TradeOrder order = new TradeOrder("AAPL", TradeOrder.OrderType.BUY, 2);
        List<DailyData> data = List.of(new DailyData("2024-02-01", 100, 110, 90, 105, 10000));

        when(stockService.fetchDailyTimeSeries("AAPL")).thenReturn(data);

        TradeResult result = portfolioService.executeTrade(order);

        assertTrue(result.isSuccess());
        assertEquals("Nákup úspěšně proveden", result.getMessage());
    }
}
