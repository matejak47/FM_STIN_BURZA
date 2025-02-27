package com.example.burza.service;

import com.example.burza.model.DailyData;
import com.example.burza.model.TradeOrder;
import com.example.burza.model.TradeResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.HashMap;
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
        portfolioService.getPortfolio().setBalance(1000.0);
        portfolioService.getPortfolio().setHoldings(new HashMap<>());
    }

    @Test
    void testExecuteBuy_Success() {
        TradeOrder order = new TradeOrder("AAPL", TradeOrder.OrderType.BUY, 2);
        List<DailyData> data = List.of(new DailyData("2024-02-01", 100, 110, 90, 105, 10000));

        when(stockService.fetchDailyTimeSeries("AAPL")).thenReturn(data);

        TradeResult result = portfolioService.executeTrade(order);

        assertTrue(result.isSuccess());
        assertEquals("Purchase successful", result.getMessage());
        assertEquals(105, result.getExecutedPrice());
        assertEquals(210, result.getTotalCost());
        assertEquals(790, result.getRemainingBalance());
        assertEquals(2, portfolioService.getPortfolio().getHoldings().get("AAPL"));
    }

    @Test
    void testExecuteBuy_InsufficientFunds() {
        TradeOrder order = new TradeOrder("AAPL", TradeOrder.OrderType.BUY, 10);
        List<DailyData> data = List.of(new DailyData("2024-02-01", 100, 110, 90, 105, 10000));

        when(stockService.fetchDailyTimeSeries("AAPL")).thenReturn(data);

        TradeResult result = portfolioService.executeTrade(order);

        assertFalse(result.isSuccess());
        assertEquals("Not enough resources for this purchase", result.getMessage());
        assertEquals(105, result.getExecutedPrice());
        assertEquals(1050, result.getTotalCost());
        assertEquals(1000, result.getRemainingBalance());
        assertFalse(portfolioService.getPortfolio().getHoldings().containsKey("AAPL"));
    }


    @Test
    void testExecuteSell_InsufficientStocks() {
        TradeOrder order = new TradeOrder("MSFT", TradeOrder.OrderType.SELL, 5);
        List<DailyData> data = List.of(new DailyData("2024-02-01", 300, 310, 290, 305, 20000));

        portfolioService.getPortfolio().getHoldings().put("MSFT", 3);

        when(stockService.fetchDailyTimeSeries("MSFT")).thenReturn(data);

        TradeResult result = portfolioService.executeTrade(order);

        assertFalse(result.isSuccess());
        assertEquals("Not enough stocks for the purchase", result.getMessage());
        assertEquals(305, result.getExecutedPrice());
        assertEquals(0, result.getTotalCost());
        assertEquals(1000, result.getRemainingBalance());
        assertEquals(3, portfolioService.getPortfolio().getHoldings().get("MSFT"));
    }

    @Test
    void testExecuteTrade_NoStockData() {
        TradeOrder order = new TradeOrder("GOOG", TradeOrder.OrderType.BUY, 2);

        when(stockService.fetchDailyTimeSeries("GOOG")).thenReturn(Collections.emptyList());

        TradeResult result = portfolioService.executeTrade(order);

        assertFalse(result.isSuccess());
        assertEquals("Impossible to get the current value of the stock", result.getMessage());
        assertEquals(0, result.getExecutedPrice());
        assertEquals(0, result.getTotalCost());
        assertEquals(1000, result.getRemainingBalance());
    }

    @Test
    void testExecuteSell_StockNotInPortfolio() {
        TradeOrder order = new TradeOrder("NVDA", TradeOrder.OrderType.SELL, 2);
        List<DailyData> data = List.of(new DailyData("2024-02-01", 700, 710, 690, 705, 5000));

        when(stockService.fetchDailyTimeSeries("NVDA")).thenReturn(data);

        TradeResult result = portfolioService.executeTrade(order);

        assertFalse(result.isSuccess());
        assertEquals("Not enough stocks for the purchase", result.getMessage());
        assertEquals(705, result.getExecutedPrice());
        assertEquals(0, result.getTotalCost());
        assertEquals(1000, result.getRemainingBalance());
    }

    @Test
    void testExecuteSell_Success() {
        TradeOrder order = new TradeOrder("AAPL", TradeOrder.OrderType.SELL, 2);
        List<DailyData> data = List.of(new DailyData("2024-02-01", 100, 110, 90, 105, 10000));

        portfolioService.getPortfolio().getHoldings().put("AAPL", 5);

        when(stockService.fetchDailyTimeSeries("AAPL")).thenReturn(data);

        TradeResult result = portfolioService.executeTrade(order);

        assertTrue(result.isSuccess());
        assertEquals("Sale successful", result.getMessage());
        assertEquals(105, result.getExecutedPrice());
        assertEquals(210, result.getTotalCost());
        assertEquals(1210, result.getRemainingBalance());
        assertEquals(3, portfolioService.getPortfolio().getHoldings().get("AAPL"));
    }

    @Test
    void testSavePortfolioState() {
        portfolioService.getPortfolio().getHoldings().put("AAPL", 3);
        portfolioService.getPortfolio().setBalance(500.0);

        // Zachytíme výstup do konzole
        System.setOut(new java.io.PrintStream(new java.io.ByteArrayOutputStream()));
        portfolioService.executeTrade(new TradeOrder("AAPL", TradeOrder.OrderType.BUY, 1));
        System.setOut(System.out);

        verify(stockService, atLeast(0)).fetchDailyTimeSeries(anyString());
    }

    @Test
    void testExecuteTrade_InvalidOrderType() {
        TradeOrder order = new TradeOrder("AAPL", null, 2);
        List<DailyData> data = List.of(new DailyData("2024-02-01", 100, 110, 90, 105, 10000));

        when(stockService.fetchDailyTimeSeries("AAPL")).thenReturn(data);

        TradeResult result = portfolioService.executeTrade(order);

        assertFalse(result.isSuccess());
        assertEquals("Invalid trade order type", result.getMessage());
    }
}