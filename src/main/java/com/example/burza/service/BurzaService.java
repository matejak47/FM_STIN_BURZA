package com.example.burza.service;

import com.example.burza.model.HistoricalData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Service handling stock exchange operations.
 * Provides functionality for fetching and filtering stock market data.
 */
@Service
public class BurzaService {
    @Value("${burza.api.url}")
    private String apiUrl;

    @Value("${burza.api.interval}")
    private String interval;

    private final RestTemplate restTemplate;

    /**
     * Constructs BurzaService with required RestTemplate.
     * @param restTemplate Template for making HTTP requests
     */
    public BurzaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetches historical data for a specific stock symbol.
     * @param symbol Stock symbol to fetch data for
     * @return List of historical data points
     */
    public List<HistoricalData> fetchHistoricalData(String symbol) {
        String url = apiUrl + "?s=" + symbol + ".US&i=" + interval;
        System.out.println("Calling API: " + url); // Debugging

        String csvData = restTemplate.getForObject(url, String.class);

        if (csvData == null || csvData.isEmpty()) {
            System.out.println("ERROR: API did not return any data!");
            return new ArrayList<>();
        }

        return parseCsvToHistoricalData(csvData);
    }

    /**
     * Filters data to show only downward price movements.
     * @param data List of historical data to filter
     * @return Filtered list showing only price decreases
     */
    public List<HistoricalData> filterDataDown(List<HistoricalData> data) {

        return data.stream()
                .filter(d -> d.getClosePrice() < d.getOpenPrice())
                .toList();
    }

    /**
     * Filters data to show only upward price movements.
     * @param data List of historical data to filter
     * @return Filtered list showing only price increases
     */

    public List<HistoricalData> filterDataUp(List<HistoricalData> data) {
        return data.stream()
                .filter(d -> d.getClosePrice() > d.getOpenPrice())
                .toList();
    }

    private List<HistoricalData> parseCsvToHistoricalData(String csvData) {
        List<HistoricalData> historicalDataList = new ArrayList<>();

        List<String> lines = new BufferedReader(new StringReader(csvData))
                .lines()
                .toList();

        if (lines.size() < 2) {
            System.out.println("ERROR: CSV does not contain any data!");
            return historicalDataList;
        }

        for (int i = 1; i < lines.size(); i++) {
            String[] values = lines.get(i).split(",");
            if (values.length >= 5) {
                String date = values[0];
                double open = Double.parseDouble(values[1]);
                double close = Double.parseDouble(values[4]);

                historicalDataList.add(new HistoricalData(date, open, close));
            }
        }

        historicalDataList.sort(Comparator.comparing(HistoricalData::getDate).reversed());

        return historicalDataList;
    }
}
