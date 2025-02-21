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

@Service
public class StockService {

    @Value("${burza.api.url}")
    private String apiUrl;

    @Value("${burza.api.interval}")
    private String interval;

    private final RestTemplate restTemplate;

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
            System.out.println("ERROR: CSV neobsahuje žádná data!");
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
}