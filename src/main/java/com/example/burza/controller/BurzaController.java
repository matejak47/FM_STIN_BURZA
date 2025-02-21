package com.example.burza.controller;

import com.example.burza.model.HistoricalData;
import com.example.burza.service.BurzaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/burza")
public class BurzaController {

    private final BurzaService burzaService;

    public BurzaController(BurzaService burzaService) {
        this.burzaService = burzaService;
    }

    // GET /api/burza/historical?symbol=IBM
    @GetMapping("/historical")
    public List<HistoricalData> getHistoricalData(@RequestParam String symbol) {
        return burzaService.fetchHistoricalData(symbol);
    }

    // POST /api/burza/filter
    // v body posílám JSON s listem HistoricalData
    @PostMapping("/filter")
    public List<HistoricalData> filterData(@RequestBody List<HistoricalData> data) {
        return burzaService.filterData(data);
    }
}
