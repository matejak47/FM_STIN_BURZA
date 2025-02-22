package com.example.burza.controller;

import com.example.burza.model.*;
import com.example.burza.service.BurzaService;
import com.example.burza.service.StockService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * Controller handling stock exchange related REST endpoints.
 * Provides functionality for retrieving and filtering stock market data.
 */
@RestController
@RequestMapping("/api/burza")
public class BurzaController {

    private final BurzaService burzaService;
    private final StockService stockService;
    private final LoadSymbols loadSymbols;

    /**
     * Constructs BurzaController with required services.
     * @param burzaService Service for handling stock exchange operations
     * @param stockService Service for handling stock-specific operations
     * @param loadSymbols Service for loading stock symbols
     */
    public BurzaController(BurzaService burzaService, StockService stockService, LoadSymbols loadSymbols) {
        this.burzaService = burzaService;
        this.stockService = stockService;
        this.loadSymbols = loadSymbols;
    }

    // --------------------------------------------------------------
    /**
     * Filters provided historical data for downward price movements.
     * @param data List of historical data to filter
     * @return Filtered list containing only downward price movements
     */
    @PostMapping("/filterdown")
    public List<HistoricalData> filterData(@RequestBody List<HistoricalData> data) {
        return burzaService.filterDataDown(data);
    }
    // --------------------------------------------------------------
    // GET /api/burza/historical?symbol=IBM
    /**
     * Retrieves historical data for a specific stock symbol.
     * @param symbol Stock symbol to retrieve data for
     * @return List of historical data for the specified symbol
     */
    @GetMapping("/historical")
    public List<HistoricalData> getHistoricalData(@RequestParam String symbol) {
        return burzaService.fetchHistoricalData(symbol);
    }

    /**
     * Retrieves and filters historical data showing downward price movements.
     * @param symbol Stock symbol to retrieve and filter data for
     * @return Filtered list of historical data showing price decreases
     */
    @GetMapping("/historical/filterDown")
    public List<HistoricalData> getFilteredHistoricalData(@RequestParam String symbol) {
        List<HistoricalData> data = burzaService.fetchHistoricalData(symbol);
        return burzaService.filterDataDown(data);
    }

    // GET /api/burza/daily?symbol=IBM
    /**
     * Retrieves daily time series data for a specific stock symbol.
     * @param symbol Stock symbol to retrieve daily data for
     * @return List of daily data points for the specified symbol
     */
    @GetMapping("/daily")
    public List<DailyData> getDailyData(@RequestParam String symbol) {
        return stockService.fetchDailyTimeSeries(symbol);
    }

    /**
     * Retrieves daily data up to a specific date.
     * @param symbol Stock symbol to retrieve data for
     * @param date End date for the data retrieval
     * @return List of daily data points up to the specified date
     */
    @GetMapping("/daily/date")
    public List<DailyData> getRecentData(@RequestParam String symbol, @RequestParam String date) {
        List<DailyData> dailyData = stockService.fetchDailyTimeSeries(symbol);
        return stockService.fetchDailyDataByTime(dailyData, date);
    }

    /**
     * Retrieves and filters historical data showing upward price movements.
     * @param symbol Stock symbol to retrieve and filter data for
     * @return Filtered list of historical data showing price increases
     */
    @GetMapping("/historical/filterUp")
    public List<HistoricalData> getFilteredUpData(@RequestParam String symbol) {
        List<HistoricalData> data = burzaService.fetchHistoricalData(symbol);
        return burzaService.filterDataUp(data);
    }

    /**
     * Retrieves all available stock symbols.
     * @return List of all available stock symbols
     * @throws IOException If there's an error loading symbols
     */
    @GetMapping("/all")
    public List<Symbol> getAllSymbols() throws IOException {
        return loadSymbols.LoadSymbols();
    }


}
