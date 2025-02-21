package com.example.burza.service;

import com.example.burza.model.HistoricalData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BurzaService {
    // (Volitelně, pokud chcete načítat z application.properties)
    // @Value("${burza.api.url}")
    // private String apiUrl;

    // @Autowired
    // private RestTemplate restTemplate;

    public List<HistoricalData> fetchHistoricalData(String symbol) {
        // Zatím klidně "mock" data:
        List<HistoricalData> result = new ArrayList<>();
        // Naplnit ukázkovými daty
        // V budoucnu: REST volání na Alpha Vantage / Finnhub / ...
        return result;
    }

    public List<HistoricalData> filterData(List<HistoricalData> data) {
        // Příklad: vrátí pouze dny, kdy došlo k poklesu (closePrice < openPrice)
        return data.stream()
                .filter(d -> d.getClosePrice() < d.getOpenPrice())
                .toList();
    }
}
