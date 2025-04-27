package com.example.burza.model;

import com.example.burza.service.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class DaysDeclineFilterTest {

    @Mock
    private StockService stockService;

    private DaysDeclineFilter daysDeclineFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        daysDeclineFilter = new DaysDeclineFilter(stockService);
    }

    @Test
    void testFilter_AllDaysDecline() {
        Symbol symbol = new Symbol("AAPL", "Apple");

        List<DailyData> dailyData = List.of(
                new DailyData("2024-04-24", 150, 140, 135, 139, 10000),
                new DailyData("2024-04-23", 155, 150, 149, 151, 12000),
                new DailyData("2024-04-22", 160, 155, 154, 156, 13000)
        );

        when(stockService.fetchDailyTimeSeries("AAPL")).thenReturn(dailyData);

        List<Symbol> result = daysDeclineFilter.filter(List.of(symbol), 3);

        assertEquals(1, result.size());
        assertEquals("AAPL", result.get(0).getSymbol());
    }

    @Test
    void testFilter_NotAllDaysDecline() {
        Symbol symbol = new Symbol("GOOG", "Google");

        List<DailyData> dailyData = List.of(
                new DailyData("2024-04-24", 100, 110, 90, 105, 15000),
                new DailyData("2024-04-23", 110, 115, 100, 112, 14000),
                new DailyData("2024-04-22", 120, 125, 110, 123, 16000)
        );

        when(stockService.fetchDailyTimeSeries("GOOG")).thenReturn(dailyData);

        List<Symbol> result = daysDeclineFilter.filter(List.of(symbol), 3);

        assertTrue(result.isEmpty());
    }

    @Test
    void testFilter_NotEnoughData() {
        Symbol symbol = new Symbol("MSFT", "Microsoft");

        List<DailyData> dailyData = List.of(
                new DailyData("2024-04-24", 300, 310, 290, 305, 17000)
        ); // pouze 1 den

        when(stockService.fetchDailyTimeSeries("MSFT")).thenReturn(dailyData);

        List<Symbol> result = daysDeclineFilter.filter(List.of(symbol), 3);

        assertTrue(result.isEmpty());
    }

    @Test
    void testFilter_MultipleSymbolsMixedResults() {
        Symbol symbol1 = new Symbol("AAPL", "Apple");
        Symbol symbol2 = new Symbol("GOOG", "Google");

        List<DailyData> aaplData = List.of(
                new DailyData("2024-04-24", 150, 140, 135, 139, 10000),
                new DailyData("2024-04-23", 155, 150, 149, 151, 12000),
                new DailyData("2024-04-22", 160, 155, 154, 156, 13000)
        );

        List<DailyData> googData = List.of(
                new DailyData("2024-04-24", 100, 110, 90, 105, 15000),
                new DailyData("2024-04-23", 110, 115, 100, 112, 14000),
                new DailyData("2024-04-22", 120, 125, 110, 123, 16000)
        );

        when(stockService.fetchDailyTimeSeries("AAPL")).thenReturn(aaplData);
        when(stockService.fetchDailyTimeSeries("GOOG")).thenReturn(googData);

        List<Symbol> result = daysDeclineFilter.filter(List.of(symbol1, symbol2), 3);

        assertEquals(1, result.size());
        assertEquals("AAPL", result.get(0).getSymbol());
    }
}
