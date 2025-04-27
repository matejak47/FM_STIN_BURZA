package com.example.burza.model;

import com.example.burza.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Filtr, který vybere společnosti,
 * které v posledních X pracovních dnech měly maximálně 2 poklesy.
 */
@Component
@RequiredArgsConstructor
public class MaxTwoDeclinesInXDaysFilter implements Filter {

    private final StockService stockService;

    public List<Symbol> filter(List<Symbol> symbols, int numberOfDays) {
        List<Symbol> result = new ArrayList<>();

        for (Symbol symbol : symbols) {
            List<DailyData> dailyData = stockService.fetchDailyTimeSeries(symbol.getSymbol());

            if (dailyData.size() < numberOfDays) {
                continue;
            }

            int declineCount = 0;

            for (int i = 0; i < numberOfDays; i++) {
                DailyData day = dailyData.get(i);
                if (day.getClose() < day.getOpen()) {
                    declineCount++;
                }
            }

            if (declineCount <= 2) {
                result.add(symbol);
            }
        }

        return result;
    }
}