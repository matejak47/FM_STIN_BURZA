package com.example.burza.service;

import com.example.burza.model.DaysDeclineFilter;
import com.example.burza.model.Symbol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
public class SchedulerService {

    @Value("${FetchTimes:0:00}")
    private String fetchTimes;

    @Value("${Days1:1}")
    private int days1; // Přidáme si days1, abychom měli stejný počet dní

    private final BurzaService burzaService;
    private final PortfolioService portfolioService;
    private final DaysDeclineFilter lastThreeDaysDeclineFilter;
    private final LoggingService loggingService;

    public SchedulerService(BurzaService burzaService,
                            PortfolioService portfolioService,
                            DaysDeclineFilter lastThreeDaysDeclineFilter,
                            LoggingService loggingService) {
        this.burzaService = burzaService;
        this.portfolioService = portfolioService;
        this.lastThreeDaysDeclineFilter = lastThreeDaysDeclineFilter;
        this.loggingService = loggingService;
    }

    @Scheduled(cron = "0 * * * * *") // každou minutu
    public void runFilterForFavoriteStocks() {
        try {
            LocalTime now = LocalTime.now().withSecond(0).withNano(0);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
            List<String> times = Arrays.asList(fetchTimes.split(";"));
            String nowFormatted = now.format(formatter);

            if (times.contains(nowFormatted)) {
                loggingService.log("Cron job triggered at: " + nowFormatted);

                List<Symbol> favoriteSymbols = portfolioService.getPortfolio().getFavoriteStocks().getSymbols();
                List<Symbol> filteredSymbols = lastThreeDaysDeclineFilter.filter(favoriteSymbols, days1);

                if (!filteredSymbols.isEmpty()) {
                    loggingService.log("Filtered symbols found, triggering transaction.");
                    portfolioService.transaction(filteredSymbols);
                } else {
                    loggingService.log("No symbols passed the filter in cron job, transaction not triggered.");
                }
            }
        } catch (Exception e) {
            System.err.println("Cron job failed safely: " + e.getMessage());
        }
    }
}
