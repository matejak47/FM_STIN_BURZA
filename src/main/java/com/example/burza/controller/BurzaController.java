package com.example.burza.controller;

import com.example.burza.model.*;
import com.example.burza.service.BurzaService;
import com.example.burza.service.StockService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/burza")
public class BurzaController {

    private final BurzaService burzaService;
    private final StockService stockService;
    private final LoadSymbols loadSymbols;


    public BurzaController(BurzaService burzaService, StockService stockService, LoadSymbols loadSymbols) {
        this.burzaService = burzaService;
        this.stockService = stockService;
        this.loadSymbols = loadSymbols;
    }

    // --------------------------------------------------------------
    @PostMapping("/filterdown")
    public List<HistoricalData> filterData(@RequestBody List<HistoricalData> data) {
        return burzaService.filterDataDown(data);
    }
    // --------------------------------------------------------------
    // GET /api/burza/historical?symbol=IBM
    @GetMapping("/historical")
    public List<HistoricalData> getHistoricalData(@RequestParam String symbol) {
        return burzaService.fetchHistoricalData(symbol);
    }

    //endpoint pro filtrovaná data!
    @GetMapping("/historical/filterDown")
    public List<HistoricalData> getFilteredHistoricalData(@RequestParam String symbol) {
        List<HistoricalData> data = burzaService.fetchHistoricalData(symbol);
        return burzaService.filterDataDown(data);
    }

    // GET /api/burza/daily?symbol=IBM
    @GetMapping("/daily")
    public List<DailyData> getDailyData(@RequestParam String symbol) {
        return stockService.fetchDailyTimeSeries(symbol);
    }

    @GetMapping("/daily/date")
    public List<DailyData> getRecentData(@RequestParam String symbol, @RequestParam String date) {
        List<DailyData> dailyData = stockService.fetchDailyTimeSeries(symbol);
        return stockService.fetchDailyDataByTime(dailyData, date);
    }

    // GET /api/burza/symbols-with-decline?days=5
    @GetMapping("/symbols-with-decline")
    public List<Symbol> getSymbolsWithDecline(@RequestParam int days) throws IOException {
        return stockService.getSymbolsWithDecline(days);
    }

    @GetMapping("/historical/filterUp")
    public List<HistoricalData> getFilteredUpData(@RequestParam String symbol) {
        List<HistoricalData> data = burzaService.fetchHistoricalData(symbol);
        return burzaService.filterDataUp(data);
    }

    @GetMapping("/all")
    public List<Symbol> getAllSymbols() throws IOException {
        return loadSymbols.LoadSymbols();
    }


}
