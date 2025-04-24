package com.example.burza.controller;

import com.example.burza.model.HistoricalData;
import com.example.burza.service.BurzaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RatingController {

    private final BurzaService burzaService;

    public RatingController(BurzaService burzaService) {
        this.burzaService = burzaService;
    }

    @GetMapping("/rating")
    public List<HistoricalData> getFilteredHistoricalData(@RequestParam String symbol) {
        List<HistoricalData> data = burzaService.fetchHistoricalData(symbol);
        return burzaService.filterDataDown(data);
    }
}
