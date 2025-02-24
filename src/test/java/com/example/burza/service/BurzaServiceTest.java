package com.example.burza.service;

import com.example.burza.model.HistoricalData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BurzaServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BurzaService burzaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFilterDataDown() {
        List<HistoricalData> data = Arrays.asList(
                new HistoricalData("2024-02-01", 100, 90),
                new HistoricalData("2024-02-02", 95, 105)
        );

        List<HistoricalData> result = burzaService.filterDataDown(data);
        assertEquals(1, result.size());
        assertEquals(90, result.get(0).getClosePrice());
    }

    @Test
    void testFilterDataUp() {
        List<HistoricalData> data = Arrays.asList(
                new HistoricalData("2024-02-01", 100, 90),
                new HistoricalData("2024-02-02", 95, 105)
        );

        List<HistoricalData> result = burzaService.filterDataUp(data);
        assertEquals(1, result.size());
        assertEquals(105, result.get(0).getClosePrice());
    }
}
