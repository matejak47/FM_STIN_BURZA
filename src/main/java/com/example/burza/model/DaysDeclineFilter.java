package com.example.burza.model;

import com.example.burza.service.StockService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Filtr, který vybere všechny společnosti,
 * které poslední 3 pracovní dny pouze klesaly (close < open).
 */
@Component
@RequiredArgsConstructor
@Data
public class DaysDeclineFilter implements Filter {

    private final StockService stockService;

    public List<Symbol> filter(List<Symbol> symbols, int numberOfDays) {
        List<Symbol> result = new ArrayList<>();

        for (Symbol symbol : symbols) {
            List<DailyData> dailyData = stockService.fetchDailyTimeSeries(symbol.getSymbol());

            if (dailyData.size() < numberOfDays) {
                continue;
            }

            boolean declinedAllDays = true;

            for (int i = 0; i < numberOfDays; i++) {
                DailyData day = dailyData.get(i);
                if (day.getClose() >= day.getOpen()) {
                    declinedAllDays = false;
                    break;
                }
            }

            if (declinedAllDays) {
                result.add(symbol);
            }
        }

        return result;
    }
}
