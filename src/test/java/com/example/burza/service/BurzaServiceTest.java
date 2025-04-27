package com.example.burza.service;

import com.example.burza.model.HistoricalData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BurzaServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BurzaService burzaService;

    private final String testApiUrl = "http://test-api.com/data";
    private final String testInterval = "1d";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(burzaService, "apiUrl", testApiUrl);
        ReflectionTestUtils.setField(burzaService, "interval", testInterval);
    }


    @Test
    void testFetchHistoricalData_Success() {
        String csvData = """
                Date,Open,High,Low,Close,Volume
                2024-02-03,150,155,145,153,10000
                2024-02-02,145,152,144,147,12000
                2024-02-01,140,148,139,145,11000""";

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(csvData);

        List<HistoricalData> result = burzaService.fetchHistoricalData("AAPL");

        assertEquals(3, result.size());

        assertEquals("2024-02-03", result.get(0).getDate());
        assertEquals("2024-02-02", result.get(1).getDate());
        assertEquals("2024-02-01", result.get(2).getDate());

        assertEquals(150, result.get(0).getOpenPrice());
        assertEquals(153, result.get(0).getClosePrice());

        verify(restTemplate).getForObject(eq(testApiUrl + "?s=AAPL.US&i=" + testInterval), eq(String.class));
    }

    @Test
    void testFetchHistoricalData_EmptyResponse() {
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn("");

        List<HistoricalData> result = burzaService.fetchHistoricalData("AAPL");

        assertTrue(result.isEmpty());
    }

    @Test
    void testFetchHistoricalData_NullResponse() {
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(null);

        List<HistoricalData> result = burzaService.fetchHistoricalData("AAPL");

        assertTrue(result.isEmpty());
    }

    @Test
    void testFetchHistoricalData_InvalidCsvFormat() {
        String csvData = "Date,Open,High,Low,Close,Volume";

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(csvData);

        List<HistoricalData> result = burzaService.fetchHistoricalData("AAPL");

        assertTrue(result.isEmpty());
    }

    @Test
    void testFetchHistoricalData_MalformedCsvData() {
        String csvData = """
                Date,Open,High,Low,Close,Volume
                2024-02-03,150,155,145
                2024-02-02,145,152,144,147,12000""";

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(csvData);

        List<HistoricalData> result = burzaService.fetchHistoricalData("AAPL");

        assertEquals(1, result.size());
        assertEquals("2024-02-02", result.get(0).getDate());
    }

    @Test
    void testFetchHistoricalData_InvalidNumberFormat() {
        String csvData = """
                Date,Open,High,Low,Close,Volume
                2024-02-03,invalid,155,145,153,10000
                2024-02-02,145,152,144,147,12000""";

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(csvData);

        assertThrows(NumberFormatException.class, () -> burzaService.fetchHistoricalData("AAPL"));
    }
}