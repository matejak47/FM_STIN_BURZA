package com.example.burza.service;

import com.example.burza.model.DailyData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class StockServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private StockService stockService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(stockService, "apiUrl", "http://example.com/api");
        ReflectionTestUtils.setField(stockService, "interval", "daily");
    }

    @Test
    void testFetchDailyTimeSeries_AsString() {
        String fakeCsvData = """
                Date,Open,High,Low,Close,Volume
                2024-02-01,100,110,90,105,10000
                2024-02-02,105,115,95,110,12000
                """;

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
    void testFetchDailyTimeSeries_EmptyStringResponse() {
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn("");

        List<DailyData> result = stockService.fetchDailyTimeSeries("AAPL");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFetchDailyTimeSeries_InvalidCsvFormat() {
        String invalidCsvData = "Date,Open,High,Low,Close,Volume";

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(invalidCsvData);

        List<DailyData> result = stockService.fetchDailyTimeSeries("AAPL");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFetchDailyTimeSeries_IncompleteValues() {
        String incompleteCsv = """
                Date,Open,High,Low,Close,Volume
                2024-02-01,100,110,90
                """;

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(incompleteCsv);

        List<DailyData> result = stockService.fetchDailyTimeSeries("AAPL");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFetchDailyTimeSeries_WithInvalidNumberFormat() {
        String csvDataWithInvalidNumber = """
                Date,Open,High,Low,Close,Volume
                2024-02-01,invalid,110,90,105,10000
                2024-02-02,105,115,95,110,12000
                """;

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(csvDataWithInvalidNumber);

        List<DailyData> result = stockService.fetchDailyTimeSeries("AAPL");

        // Ověříme že špatný řádek je ignorován a správný zůstává
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(110, result.get(0).getClose());
    }

    @Test
    void testFetchDailyTimeSeries_NullRawList() {
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(null);
        when(restTemplate.getForObject(anyString(), eq(List.class)))
                .thenReturn(null); // simulate null list

        List<DailyData> result = stockService.fetchDailyTimeSeries("AAPL");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFetchDailyTimeSeries_EmptyRawList() {
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(null);
        when(restTemplate.getForObject(anyString(), eq(List.class)))
                .thenReturn(List.of()); // simulate empty list

        List<DailyData> result = stockService.fetchDailyTimeSeries("AAPL");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFetchDailyTimeSeries_NullFirstElementInRawList() {
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(null);

        List<String> listWithNull = new ArrayList<>();
        listWithNull.add(null);

        when(restTemplate.getForObject(anyString(), eq(List.class)))
                .thenReturn(listWithNull);

        List<DailyData> result = stockService.fetchDailyTimeSeries("AAPL");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testParseCsvToDailyData_ArrayIndexOutOfBounds() {
        String csvWithTooFewColumns = """
                Date,Open,High,Low,Close,Volume
                2024-02-01,100,110
                """; // jen 3 hodnoty místo 6!

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(csvWithTooFewColumns);

        List<DailyData> result = stockService.fetchDailyTimeSeries("AAPL");

        assertNotNull(result);
        assertTrue(result.isEmpty()); // chyba zachycena catch blokem
    }


}
