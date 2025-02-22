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
     * @param restTemplate Template for making HTTP requests
     */
    public StockService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Method for downloading daily data and its conversion to JSON format.
     * @param symbol Ticker symbol stock.
     * @return  JSON list containing all daily data.
     */
    public List<DailyData> fetchDailyTimeSeries(String symbol) {
        // Assembly of URL for gathering data
        String url = apiUrl + "?s=" + symbol + ".US&i=" + interval;

        // Download of CSV data
        String csvData = restTemplate.getForObject(url, String.class);

        // Conversion from CSV to JSON containing all details
        return parseCsvToDailyData(csvData);
    }

    /**
     * Retrieves all stock symbols that have shown a price decline over the specified number of days.
     * @param days Number of days to check for price decline
     * @return List of symbols that have declined in price
     */
    public List<String> getSymbolsWithDecline(List<String> symbols,int days){
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
     * Fetches daily data up to a specific start date.
     * @param dailyDataList Complete list of daily data
     * @param startDate Date to start fetching data from
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
        System.out.println(dateIndex);
        List<DailyData> recentDataList = new ArrayList<>();
        for (int i = 0; i < dateIndex; i++){
            recentDataList.add(dailyDataList.get(i));
        }
        return recentDataList;
    }

    /**
     * Helper method for conversion from CSV to JSON
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
                } catch (NumberFormatException e) {
                    System.out.println("Error parsing values: " + String.join(", ", values));
                    e.printStackTrace();
                }
            }
        }

        // Sorting based on the date from newest to oldest
        dailyDataList.sort(Comparator.comparing(DailyData::getDate).reversed());

        return dailyDataList;
    }

    /**
     * Checks if there has been a price decline over the specified number of days.
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

        boolean decline = lastDay.getClose() > firstDay.getClose();

        return decline;
    }
}