package com.example.burza.model;

import com.example.burza.service.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class MaxTwoDeclinesInXDaysFilterTest {

    private StockService stockService;
    private MaxTwoDeclinesInXDaysFilter filter;

    @BeforeEach
    void setUp() {
        stockService = Mockito.mock(StockService.class);
        filter = new MaxTwoDeclinesInXDaysFilter(stockService);
    }

    @Test
    void filter_ShouldReturnSymbol_WhenMaxTwoDeclines() {
        Symbol testSymbol = new Symbol("AAPL", "Apple");

        // Připrav DailyData: 5 dní, jen 2 poklesy
        List<DailyData> dailyData = List.of(
                new DailyData("2024-04-26", 150, 155, 149, 152, 1000000), // růst
                new DailyData("2024-04-25", 155, 156, 154, 153, 1000000), // pokles
                new DailyData("2024-04-24", 152, 157, 151, 155, 1000000), // růst
                new DailyData("2024-04-23", 155, 156, 154, 153, 1000000), // pokles
                new DailyData("2024-04-22", 153, 154, 152, 155, 1000000)  // růst
        );

        when(stockService.fetchDailyTimeSeries(testSymbol.getSymbol())).thenReturn(dailyData);

        List<Symbol> result = filter.filter(List.of(testSymbol), 5);

        assertEquals(1, result.size());
        assertEquals("AAPL", result.get(0).getSymbol());
    }

    @Test
    void filter_ShouldNotReturnSymbol_WhenMoreThanTwoDeclines() {
        Symbol testSymbol = new Symbol("GOOG", "Google");

        // Připrav DailyData: 5 dní, 3 poklesy
        List<DailyData> dailyData = List.of(
                new DailyData("2024-04-26", 150, 155, 149, 148, 1000000), // pokles
                new DailyData("2024-04-25", 148, 156, 147, 145, 1000000), // pokles
                new DailyData("2024-04-24", 145, 157, 144, 143, 1000000), // pokles
                new DailyData("2024-04-23", 143, 156, 142, 145, 1000000), // růst
                new DailyData("2024-04-22", 145, 154, 144, 147, 1000000)  // růst
        );

        when(stockService.fetchDailyTimeSeries(testSymbol.getSymbol())).thenReturn(dailyData);

        List<Symbol> result = filter.filter(List.of(testSymbol), 5);

        assertEquals(0, result.size());
    }

    @Test
    void filter_ShouldSkipSymbol_WhenNotEnoughData() {
        Symbol testSymbol = new Symbol("MSFT", "Microsoft");

        // Připrav DailyData: pouze 3 dny
        List<DailyData> dailyData = List.of(
                new DailyData("2024-04-26", 150, 155, 149, 152, 1000000),
                new DailyData("2024-04-25", 152, 156, 151, 154, 1000000),
                new DailyData("2024-04-24", 154, 157, 153, 156, 1000000)
        );

        when(stockService.fetchDailyTimeSeries(testSymbol.getSymbol())).thenReturn(dailyData);

        List<Symbol> result = filter.filter(List.of(testSymbol), 5); // Chceme 5 dní, máme jen 3

        assertEquals(0, result.size());
    }
}
