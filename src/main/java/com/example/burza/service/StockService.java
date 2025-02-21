package com.example.burza.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class StockService {
    @Value("${burza.api.url}")
    private String apiUrl;



    @Autowired
    private RestTemplate restTemplate;

    public String fetchDailyTimeSeries(String symbol) {
        String url = apiUrl + "?function=TIME_SERIES_DAILY&symbol=" + symbol + "&apikey=";
        return restTemplate.getForObject(url, String.class);
    }

}
