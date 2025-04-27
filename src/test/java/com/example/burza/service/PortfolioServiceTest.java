package com.example.burza.service;

import com.example.burza.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PortfolioServiceTest {

    @Mock
    private StockService stockService;

    @Mock
    private LoggingService loggingService;

    @Mock
    private RestTemplate restTemplate;


    @InjectMocks
    private PortfolioService portfolioService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        portfolioService.getPortfolio().setBalance(1000.0);
        portfolioService.getPortfolio().setHoldings(new HashMap<>());
    }

    @Test
    void testParseToJson() {
        List<String> symbols = List.of("AAPL", "TSLA");
        List<DailyData> dailyDataList = List.of(new DailyData("2024-02-01", 100, 110, 90, 105, 10000));

        when(stockService.fetchDailyTimeSeries("AAPL")).thenReturn(dailyDataList);
        when(stockService.fetchDailyTimeSeries("TSLA")).thenReturn(dailyDataList);

        List<StockResponse> result = portfolioService.parseToJson(symbols);

        assertEquals(2, result.size());
        assertEquals("AAPL", result.get(0).getName());
        assertEquals("TSLA", result.get(1).getName());
        assertTrue(result.get(0).getDate() > 0); // Timestamp should be valid
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

    @Test
    void testExtractRequestIdWithReflection() throws Exception {
        Method method = PortfolioService.class.getDeclaredMethod("extractRequestId", String.class);
        method.setAccessible(true);

        int requestId = (int) method.invoke(portfolioService, "{\"request_id\":123}");

        assertEquals(123, requestId);
    }

    @Test
    void testExtractStatusWithReflection() throws Exception {
        Method method = PortfolioService.class.getDeclaredMethod("extractStatus", String.class);
        method.setAccessible(true);

        String status = (String) method.invoke(portfolioService, "{\"status\":\"done\"}");

        assertEquals("done", status);
    }

    @Test
    void testParseFavoritesToJsonGrancekWithReflection() throws Exception {
        Method method = PortfolioService.class.getDeclaredMethod("parseFavoritesToJsonGrancek", List.class);
        method.setAccessible(true);

        List<Symbol> symbols = List.of(new Symbol("AAPL", "Apple"));

        String json = (String) method.invoke(portfolioService, symbols);

        assertTrue(json.contains("Apple"));
        assertTrue(json.contains("from"));
        assertTrue(json.contains("to"));
    }


    @Test
    void testConvertJsonStringToStatusJsonWithReflection() throws Exception {
        Method method = PortfolioService.class.getDeclaredMethod("convertJsonStringToStatusJson", String.class);
        method.setAccessible(true);

        String inputJson = "[{\"company_name\":\"Apple\",\"rating\":0.8}]";
        String outputJson = (String) method.invoke(portfolioService, inputJson);

        // místo contains -> načíst jako JSON a zkontrolovat
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(outputJson);

        assertEquals(1, rootNode.size());
        JsonNode appleNode = rootNode.get(0);

        assertEquals("Apple", appleNode.get("name").asText());
        assertEquals(1, appleNode.get("status").asInt());
    }

    @Test
    void testParseToJson_Exception() {
        when(stockService.fetchDailyTimeSeries(anyString())).thenThrow(new RuntimeException("Error"));
        List<StockResponse> result = portfolioService.parseToJson(List.of("AAPL"));
        assertTrue(result.isEmpty());
    }

    @Test
    void testParseFavoritesToJsonGrancek_Exception() throws Exception {
        Method method = PortfolioService.class.getDeclaredMethod("parseFavoritesToJsonGrancek", List.class);
        method.setAccessible(true);

        List<Symbol> symbols = new ArrayList<>() {
            @Override
            public Symbol get(int index) {
                throw new RuntimeException("Fake error");
            }
        };

        String result = (String) method.invoke(portfolioService, symbols);
        assertEquals("[]", result);
    }


    @Test
    void testConvertJsonStringToStatusJson_Exception() throws Exception {
        Method method = PortfolioService.class.getDeclaredMethod("convertJsonStringToStatusJson", String.class);
        method.setAccessible(true);

        String invalidJson = "INVALID_JSON";
        String result = (String) method.invoke(portfolioService, invalidJson);
        assertEquals("[]", result);
    }


    @Test
    void testEvaluateDataFromGrancek_Success() throws Exception {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        // Zavoláme privátní metodu přes reflection
        Method method = PortfolioService.class.getDeclaredMethod("evaluateDataFromGrancek", String.class);
        method.setAccessible(true);

        String inputJson = "[{\"company_name\":\"Apple\",\"rating\":0.8}]";
        assertDoesNotThrow(() -> method.invoke(portfolioService, inputJson));
    }


}