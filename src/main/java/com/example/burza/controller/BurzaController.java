package com.example.burza.controller;

import com.example.burza.model.HistoricalData;
import com.example.burza.service.BurzaService;
import com.example.burza.service.StockService;
import org.springframework.web.bind.annotation.*;



import java.util.List;

@RestController
@RequestMapping("/api/burza")
public class BurzaController {

    private final BurzaService burzaService;
    private final StockService stockService;
    public BurzaController(BurzaService burzaService, StockService stockService) {
        this.burzaService = burzaService;
        this.stockService = stockService;
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

    @GetMapping("/daily")
    public String getDailyData(@RequestParam String symbol) {
        return stockService.fetchDailyTimeSeries(symbol);
    }
}
