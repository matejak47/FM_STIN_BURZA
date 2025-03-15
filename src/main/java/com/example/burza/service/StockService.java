package com.example.burza.service;

import com.example.burza.model.DailyData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Service class for handling stock-related operations.
 * Provides functionality for fetching and analyzing stock market data.
 */
@Service
public class StockService {

    @Value("${burza.api.url}")
    private String apiUrl;

    @Value("${burza.api.interval}")
    private String interval;

    private final RestTemplate restTemplate;

    /**
     * Constructs StockService with required RestTemplate.
     *
     * @param restTemplate Template for making HTTP requests
     */
    public StockService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Method for downloading daily data and its conversion to JSON format.
     *
     * @param symbol Ticker symbol stock.
     * @return JSON list containing all daily data.
     */
    public List<DailyData> fetchDailyTimeSeries(String symbol) {
        String url = apiUrl + "?s=" + symbol + ".US&i=" + interval;
        String csvData = restTemplate.getForObject(url, String.class);

        if (csvData == null || csvData.isEmpty()) {
            List<?> rawList = restTemplate.getForObject(url, List.class);
            List<String> listResponse = null;
            if (rawList != null && !rawList.isEmpty()) {
                listResponse = new ArrayList<>();
                for (Object raw : rawList) {
                    if (raw instanceof String) {
                        listResponse.add((String) raw);
                    }
                }
            }

            if (listResponse != null && !listResponse.isEmpty()) {
                if (listResponse.get(0) != null) {
                    csvData = String.join("\n", listResponse);
                } else {
                    System.out.println("ERROR: Expected format is: List<String>, but API returned: " + listResponse.get(0).getClass().getSimpleName());
                    return List.of();
                }
            }
        }

        if (csvData == null || csvData.isEmpty()) {
            System.out.println("ERROR: API did not return any data!");
            return List.of();
        }

        return parseCsvToDailyData(csvData);
    }


    /**
     * Retrieves all stock symbols that have shown a price decline over the specified number of days.
     *
     * @param days    Number of days to check for price decline
     * @param symbols Favourite symbols of the user
     * @return List of symbols that have declined in price
     */
    public List<String> getSymbolsWithDecline(List<String> symbols, int days) {
        List<String> decliningSymbols = new ArrayList<>();

        for (String symbol : symbols) {
            List<DailyData> data = fetchDailyTimeSeries(symbol);

            if (hasDeclineInLastNDays(data, days)) {
                decliningSymbols.add(symbol);
            }
        }

        return decliningSymbols;
    }

    /**
     * Retrieves all stock symbols that have shown a price increase over the specified number of days.
     *
     * @param days    Number of days to check for price increase
     * @param symbols Favourite symbols of the user
     * @return List of symbols that have increased in price
     */
    public List<String> getSymbolsWithIncrease(List<String> symbols, int days) {
        List<String> increasingSymbols = new ArrayList<>();

        for (String symbol : symbols) {
            List<DailyData> data = fetchDailyTimeSeries(symbol);

            if (hasIncreasedInLastNDays(data, days)) {
                increasingSymbols.add(symbol);
            }
        }

        return increasingSymbols;
    }


    /**
     * Fetches daily data up to a specific start date.
     *
     * @param dailyDataList Complete list of daily data
     * @param startDate     Date to start fetching data from
     * @return List of daily data points up to the specified date
     */
    public List<DailyData> fetchDailyDataByTime(List<DailyData> dailyDataList, String startDate) {
        int dateIndex = 0;
        for (int i = 0; i <= dailyDataList.size(); i++) {
            if (dailyDataList.get(i).getDate().equals(startDate)) {
                dateIndex = i;
                break;
            }
        }
        List<DailyData> recentDataList = new ArrayList<>();
        for (int i = 0; i < dateIndex; i++) {
            recentDataList.add(dailyDataList.get(i));
        }
        return recentDataList;
    }

    /**
     * Helper method for conversion from CSV to JSON
     *
     * @param csvData CSV data to string.
     * @return JSON representation of data.
     */
    private List<DailyData> parseCsvToDailyData(String csvData) {
        List<DailyData> dailyDataList = new ArrayList<>();

        // Reading of CSV lines
        List<String> lines = new BufferedReader(new StringReader(csvData))
                .lines()
                .toList();

        if (lines.size() < 2) {
            System.out.println("ERROR: CSV does not contain any data!");
            return dailyDataList;
        }

        for (int i = 1; i < lines.size(); i++) {
            String[] values = lines.get(i).split(",");

            if (values.length >= 6) {
                try {
                    String date = values[0];
                    double open = Double.parseDouble(values[1]);
                    double high = Double.parseDouble(values[2]);
                    double low = Double.parseDouble(values[3]);
                    double close = Double.parseDouble(values[4]);
                    long volume = (long) Double.parseDouble(values[5]);

                    dailyDataList.add(new DailyData(date, open, high, low, close, volume));
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("Warning: Skipping invalid CSV line. Values: " + String.join(", ", values));
                }
            }
        }

        // Sorting based on the date from newest to oldest
        dailyDataList.sort(Comparator.comparing(DailyData::getDate).reversed());

        return dailyDataList;
    }

    /**
     * Checks if there has been a price decline over the specified number of days.
     *
     * @param data List of daily data to analyze
     * @param days Number of days to check for decline
     * @return True if there has been a price decline, false otherwise
     */
    private boolean hasDeclineInLastNDays(List<DailyData> data, int days) {
        if (data == null || data.size() < days) {
            return false;
        }

        DailyData firstDay = data.get(0);
        DailyData lastDay = data.get(days - 1);

        return lastDay.getClose() > firstDay.getClose();
    }

    private boolean hasIncreasedInLastNDays(List<DailyData> data, int days) {
        if (data == null || data.size() < days) {
            return false;
        }

        DailyData firstDay = data.get(0);
        DailyData lastDay = data.get(days - 1);

        return lastDay.getClose() < firstDay.getClose();
    }
}