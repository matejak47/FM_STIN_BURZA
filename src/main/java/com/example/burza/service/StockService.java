package com.example.burza.service;

import com.example.burza.model.DailyData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
     * Metoda pro stažení denních dat a jejich převod do JSON formátu.
     * @param symbol Ticker symbol akcie.
     * @return JSON seznam obsahující všechna denní data.
     */
    public List<DailyData> fetchDailyTimeSeries(String symbol) {
        // Sestavení URL pro získání dat
        String url = apiUrl + "?s=" + symbol + ".US&i=" + interval;
        System.out.println("Calling API: " + url); // Debugging

        // Stažení CSV dat
        String csvData = restTemplate.getForObject(url, String.class);

        // Převod CSV na JSON obsahující všechna údaje
        return parseCsvToDailyData(csvData);
    }

    /**
     * Pomocná metoda pro převod CSV dat na JSON.
     * @param csvData CSV data jako řetězec.
     * @return JSON reprezentace dat.
     */
    private List<DailyData> parseCsvToDailyData(String csvData) {
        List<DailyData> dailyDataList = new ArrayList<>();

        // Čtení CSV řádků (vynechání hlavičky)
        List<String> lines = new BufferedReader(new StringReader(csvData))
                .lines()
                .collect(Collectors.toList());

        if (lines.size() < 2) {
            System.out.println("ERROR: CSV neobsahuje žádná data!");
            return dailyDataList;  // Pokud jsou data prázdná, vracíme prázdný seznam
        }

        // Debug: Zobrazit každý řádek CSV dat
        for (String line : lines) {
            System.out.println("Line: " + line);  // Zobrazí každý řádek
        }

        // Iterace přes CSV řádky a převod na objekty DailyData
        for (int i = 1; i < lines.size(); i++) { // Přeskakujeme první řádek (hlavičku)
            String[] values = lines.get(i).split(",");

            // Debug: Zobrazit hodnoty pro každý řádek
            System.out.println("Parsed values: " + String.join(", ", values));

            if (values.length >= 6) {
                try {
                    String date = values[0];
                    double open = Double.parseDouble(values[1]);
                    double high = Double.parseDouble(values[2]);
                    double low = Double.parseDouble(values[3]);
                    double close = Double.parseDouble(values[4]);
                    long volume = (long) Double.parseDouble(values[5]);  // Volume může mít desetinné hodnoty, takže je lepší ho převést na long

                    dailyDataList.add(new DailyData(date, open, high, low, close, volume));
                } catch (NumberFormatException e) {
                    System.out.println("Error parsing values: " + String.join(", ", values));
                    e.printStackTrace();
                }
            }
        }

        // Seřazení podle data od nejnovějšího po nejstarší
        dailyDataList.sort(Comparator.comparing(DailyData::getDate).reversed());

        return dailyDataList; // Vracíme seznam DailyData
    }



}
