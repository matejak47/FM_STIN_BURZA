package com.example.burza.service;

import com.example.burza.model.HistoricalData;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BurzaService {
    @Value("${burza.api.url}")
    private String apiUrl;

    @Value("${burza.api.interval}")
    private String interval;

    private final RestTemplate restTemplate;

    public BurzaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<HistoricalData> fetchHistoricalData(String symbol) {
        //Sestavení URL pro Stooq API
        String url = apiUrl + "?s=" + symbol + ".US&i=" + interval;
        System.out.println("Calling API: " + url); // Debugging

        //Zavoláme API a získáme odpověď jako CSV
        String csvData = restTemplate.getForObject(url, String.class);

        if (csvData == null || csvData.isEmpty()) {
            System.out.println("ERROR: API nevrátilo žádná data!");
            return new ArrayList<>();
        }

        //Převod CSV na JSON
        return parseCsvToHistoricalData(csvData);
    }

    public List<HistoricalData> filterDataDown(List<HistoricalData> data) {
        //Filtruje pouze dny, kdy cena klesla
        return data.stream()
                .filter(d -> d.getClosePrice() < d.getOpenPrice())
                .toList();
    }

    public List<HistoricalData> filterDataUp(List<HistoricalData> data) {
        //Filtruje pouze dny, kdy cena klesla
        return data.stream()
                .filter(d -> d.getClosePrice() > d.getOpenPrice())
                .toList();
    }


    private List<HistoricalData> parseCsvToHistoricalData(String csvData) {
        List<HistoricalData> historicalDataList = new ArrayList<>();

        //Čtení CSV řádků (vynechání hlavičky)
        List<String> lines = new BufferedReader(new StringReader(csvData))
                .lines()
                .collect(Collectors.toList());

        if (lines.size() < 2) {
            System.out.println("ERROR: CSV neobsahuje žádná data!");
            return historicalDataList;
        }

        for (int i = 1; i < lines.size(); i++) { // Přeskakujeme první řádek (hlavičku)
            String[] values = lines.get(i).split(",");
            if (values.length >= 5) {
                String date = values[0];
                double open = Double.parseDouble(values[1]);
                double close = Double.parseDouble(values[4]);

                historicalDataList.add(new HistoricalData(date, open, close));
            }
        }

        //**Seřadíme podle datumu od NEJNOVĚJŠÍHO po NEJSTARŠÍ**
        historicalDataList.sort(Comparator.comparing(HistoricalData::getDate).reversed());

        return historicalDataList;
    }
}
