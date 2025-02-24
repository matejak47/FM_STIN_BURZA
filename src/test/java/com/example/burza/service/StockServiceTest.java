package com.example.burza.service;

import com.example.burza.model.DailyData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class StockServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Spy
    @InjectMocks
    private StockService stockService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFetchDailyTimeSeries_AsString() {
        String fakeCsvData = "Date,Open,High,Low,Close,Volume\n" +
                "2024-02-01,100,110,90,105,10000\n" +
                "2024-02-02,105,115,95,110,12000\n";

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(fakeCsvData);

        List<DailyData> result = stockService.fetchDailyTimeSeries("AAPL");

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals(110, result.get(0).getClose());
    }

    @Test
    void testFetchDailyTimeSeries_AsList() {
        List<String> fakeCsvDataAsList = List.of(
                "Date,Open,High,Low,Close,Volume",
                "2024-02-01,100,110,90,105,10000",
                "2024-02-02,105,115,95,110,12000"
        );

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(null);
        when(restTemplate.getForObject(anyString(), eq(List.class)))
                .thenReturn(fakeCsvDataAsList);

        List<DailyData> result = stockService.fetchDailyTimeSeries("AAPL");

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals(110, result.get(0).getClose());
    }

    @Test
    void testFetchDailyTimeSeries_NullResponse() {
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(null);
        when(restTemplate.getForObject(anyString(), eq(List.class)))
                .thenReturn(null);

        List<DailyData> result = stockService.fetchDailyTimeSeries("AAPL");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetSymbolsWithDecline() {
        // Příprava testovacích dat
        List<String> symbols = List.of("AAPL", "MSFT", "GOOG");
        int days = 7;

        // Data pro GOOG - stabilní trend
        List<DailyData> googData = new ArrayList<>();
        googData.add(new DailyData("2024-02-07", 150.0, 151.0, 149.0, 150.0, 30000));
        googData.add(new DailyData("2024-02-06", 150.0, 151.0, 149.0, 150.0, 29000));
        googData.add(new DailyData("2024-02-05", 150.0, 151.0, 149.0, 150.0, 28000));
        googData.add(new DailyData("2024-02-04", 150.0, 151.0, 149.0, 150.0, 27000));
        googData.add(new DailyData("2024-02-03", 150.0, 151.0, 149.0, 150.0, 26000));
        googData.add(new DailyData("2024-02-02", 150.0, 151.0, 149.0, 150.0, 25000));
        googData.add(new DailyData("2024-02-01", 150.0, 151.0, 149.0, 150.0, 24000));

        // Data pro MSFT - stabilní trend
        List<DailyData> msftData = new ArrayList<>();
        msftData.add(new DailyData("2024-02-07", 250.0, 252.0, 249.0, 250.0, 20000));
        msftData.add(new DailyData("2024-02-06", 250.0, 252.0, 249.0, 250.0, 20000));
        msftData.add(new DailyData("2024-02-05", 250.0, 252.0, 249.0, 250.0, 20000));
        msftData.add(new DailyData("2024-02-04", 250.0, 252.0, 249.0, 250.0, 20000));
        msftData.add(new DailyData("2024-02-03", 250.0, 252.0, 249.0, 250.0, 20000));
        msftData.add(new DailyData("2024-02-02", 250.0, 252.0, 249.0, 250.0, 20000));
        msftData.add(new DailyData("2024-02-01", 250.0, 252.0, 249.0, 250.0, 20000));

        // Data pro AAPL - klesající trend
        List<DailyData> aaplData = new ArrayList<>();
        aaplData.add(new DailyData("2024-02-07", 190.0, 191.0, 189.0, 190.0, 10000));
        aaplData.add(new DailyData("2024-02-06", 191.0, 192.0, 190.0, 191.0, 11000));
        aaplData.add(new DailyData("2024-02-05", 192.0, 193.0, 191.0, 192.0, 12000));
        aaplData.add(new DailyData("2024-02-04", 193.0, 194.0, 192.0, 193.0, 13000));
        aaplData.add(new DailyData("2024-02-03", 194.0, 195.0, 193.0, 194.0, 14000));
        aaplData.add(new DailyData("2024-02-02", 195.0, 196.0, 194.0, 195.0, 15000));
        aaplData.add(new DailyData("2024-02-01", 196.0, 197.0, 195.0, 196.0, 16000));

        // Use spy instead of mock to partially mock the stockService
        doReturn(googData).when(stockService).fetchDailyTimeSeries("GOOG");
        doReturn(msftData).when(stockService).fetchDailyTimeSeries("MSFT");
        doReturn(aaplData).when(stockService).fetchDailyTimeSeries("AAPL");

        // Spuštění testované metody
        List<String> result = stockService.getSymbolsWithDecline(symbols, days);

        // Ověření výsledků - pouze AAPL by měl mít klesající trend
        assertEquals(1, result.size());
        assertTrue(result.contains("AAPL"));
        assertFalse(result.contains("MSFT"));
        assertFalse(result.contains("GOOG"));
    }

    @Test
    void testGetSymbolsWithIncrease() {
        // Příprava testovacích dat
        List<String> symbols = List.of("AAPL", "MSFT", "GOOG");
        int days = 7;

        // Data pro GOOG - rostoucí trend
        List<DailyData> googData = new ArrayList<>();
        googData.add(new DailyData("2024-02-07", 150.0, 151.0, 149.0, 150.0, 30000));
        googData.add(new DailyData("2024-02-06", 149.0, 150.0, 148.0, 149.0, 29000));
        googData.add(new DailyData("2024-02-05", 148.0, 149.0, 147.0, 148.0, 28000));
        googData.add(new DailyData("2024-02-04", 147.0, 148.0, 146.0, 147.0, 27000));
        googData.add(new DailyData("2024-02-03", 146.0, 147.0, 145.0, 146.0, 26000));
        googData.add(new DailyData("2024-02-02", 145.0, 146.0, 144.0, 145.0, 25000));
        googData.add(new DailyData("2024-02-01", 144.0, 145.0, 143.0, 144.0, 24000));

        // Data pro MSFT - stabilní trend
        List<DailyData> msftData = new ArrayList<>();
        msftData.add(new DailyData("2024-02-07", 250.0, 252.0, 249.0, 250.0, 20000));
        msftData.add(new DailyData("2024-02-06", 250.0, 252.0, 249.0, 250.0, 20000));
        msftData.add(new DailyData("2024-02-05", 250.0, 252.0, 249.0, 250.0, 20000));
        msftData.add(new DailyData("2024-02-04", 250.0, 252.0, 249.0, 250.0, 20000));
        msftData.add(new DailyData("2024-02-03", 250.0, 252.0, 249.0, 250.0, 20000));
        msftData.add(new DailyData("2024-02-02", 250.0, 252.0, 249.0, 250.0, 20000));
        msftData.add(new DailyData("2024-02-01", 250.0, 252.0, 249.0, 250.0, 20000));

        // Data pro AAPL - klesající trend
        List<DailyData> aaplData = new ArrayList<>();
        aaplData.add(new DailyData("2024-02-07", 190.0, 191.0, 189.0, 190.0, 10000));
        aaplData.add(new DailyData("2024-02-06", 191.0, 192.0, 190.0, 191.0, 11000));
        aaplData.add(new DailyData("2024-02-05", 192.0, 193.0, 191.0, 192.0, 12000));
        aaplData.add(new DailyData("2024-02-04", 193.0, 194.0, 192.0, 193.0, 13000));
        aaplData.add(new DailyData("2024-02-03", 194.0, 195.0, 193.0, 194.0, 14000));
        aaplData.add(new DailyData("2024-02-02", 195.0, 196.0, 194.0, 195.0, 15000));
        aaplData.add(new DailyData("2024-02-01", 196.0, 197.0, 195.0, 196.0, 16000));

        // Use spy instead of mock to partially mock the stockService
        doReturn(googData).when(stockService).fetchDailyTimeSeries("GOOG");
        doReturn(msftData).when(stockService).fetchDailyTimeSeries("MSFT");
        doReturn(aaplData).when(stockService).fetchDailyTimeSeries("AAPL");

        // Spuštění testované metody
        List<String> result = stockService.getSymbolsWithIncrease(symbols, days);

        // Ověření výsledků - pouze GOOG by měl mít rostoucí trend
        assertEquals(1, result.size());
        assertTrue(result.contains("GOOG"));
        assertFalse(result.contains("MSFT"));
        assertFalse(result.contains("AAPL"));
    }
    @Test
    void testFetchDailyDataByTime() {
        // Prepare test data
        List<DailyData> completeDataList = new ArrayList<>();
        completeDataList.add(new DailyData("2024-02-07", 150.0, 151.0, 149.0, 150.0, 30000));
        completeDataList.add(new DailyData("2024-02-06", 149.0, 150.0, 148.0, 149.0, 29000));
        completeDataList.add(new DailyData("2024-02-05", 148.0, 149.0, 147.0, 148.0, 28000));
        completeDataList.add(new DailyData("2024-02-04", 147.0, 148.0, 146.0, 147.0, 27000));
        completeDataList.add(new DailyData("2024-02-03", 146.0, 147.0, 145.0, 146.0, 26000));

        // Define a start date that exists in the dataset
        String startDate = "2024-02-05";

        // Execute the method
        List<DailyData> result = stockService.fetchDailyDataByTime(completeDataList, startDate);

        // Assertions
        assertNotNull(result);
        assertEquals(2, result.size()); // Should contain data from 2024-02-07 and 2024-02-06

        // Verify the dates of the returned data
        assertEquals("2024-02-07", result.get(0).getDate());
        assertEquals("2024-02-06", result.get(1).getDate());

        // Verify the returned data doesn't contain the start date or any dates after it
        for (DailyData data : result) {
            assertNotEquals(startDate, data.getDate());
        }
    }
}