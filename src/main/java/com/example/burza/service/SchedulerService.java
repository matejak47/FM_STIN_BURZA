package com.example.burza.service;

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

    private final BurzaService burzaService;
    private final PortfolioService portfolioService;

    public SchedulerService(BurzaService burzaService, PortfolioService portfolioService) {
        this.burzaService = burzaService;
        this.portfolioService = portfolioService;
    }

    @Scheduled(cron = "0 * * * * *") // každou minutu
    public void runFilterForFavoriteStocks() {
        try {
            LocalTime now = LocalTime.now().withSecond(0).withNano(0);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
            List<String> times = Arrays.asList(fetchTimes.split(";"));
            String nowFormatted = now.format(formatter);

            if (times.contains(nowFormatted)) {
                List<Symbol> favoriteSymbols = portfolioService.getPortfolio().getFavoriteStocks().getSymbols();

                // TODO: Zde implementovat akci, která se má provést v nastavených časech
                System.out.println("Trigger action for favorite symbols at: " + nowFormatted);
            }
        } catch (Exception e) {
            System.err.println("Cron job failed safely: " + e.getMessage());
        }
    }
}
