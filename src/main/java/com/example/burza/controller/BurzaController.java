package com.example.burza.controller;

import com.example.burza.model.DailyData;
import com.example.burza.model.Symbol;
import com.example.burza.model.SymbolLoading;
import com.example.burza.service.BurzaService;
import com.example.burza.service.StockService;
import org.springframework.http.HttpStatus;
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
    private final SymbolLoading symbolLoading;

    /**
     * Constructs BurzaController with required services.
     *
     * @param burzaService  Service for handling stock exchange operations
     * @param stockService  Service for handling stock-specific operations
     * @param symbolLoading Service for loading stock symbols
     */
    public BurzaController(BurzaService burzaService, StockService stockService, SymbolLoading symbolLoading) {
        this.burzaService = burzaService;
        this.stockService = stockService;
        this.symbolLoading = symbolLoading;
    }

    /**
     * Retrieves daily time series data for a specific stock symbol.
     *
     * @param symbol Stock symbol to retrieve daily data for
     * @return List of daily data points for the specified symbol
     */
    @GetMapping("/daily")
    public List<DailyData> getDailyData(@RequestParam String symbol) {
        return stockService.fetchDailyTimeSeries(symbol);
    }

    /**
     * Retrieves all available stock symbols.
     *
     * @return List of all available stock symbols
     */
    @GetMapping("/all")
    public List<Symbol> getAllSymbols() {
        try {
            return symbolLoading.LoadSymbols();
        } catch (IOException e) {
            throw new RuntimeException("Error loading symbols", e);
        }
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleRuntimeException() {
        return "Error loading symbols";
    }


}
