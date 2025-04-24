package com.example.burza.service;

import com.example.burza.model.HistoricalData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
public class SchedulerService {

    @Value("${FetchTimes}")
    private String fetchTimes;

    private final BurzaService burzaService;
    private final PortfolioService portfolioService;

    public SchedulerService(BurzaService burzaService, PortfolioService portfolioService) {
        this.burzaService = burzaService;
        this.portfolioService = portfolioService;
    }

    @Scheduled(cron = "0 * * * * *") // každou minutu
    public void runFilterForFavoriteStocks() {
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");

        List<String> times = Arrays.asList(fetchTimes.split(";"));
        String nowFormatted = now.format(formatter);

        if (times.contains(nowFormatted)) {
            List<String> favoriteSymbols = portfolioService.getPortfolio().getFavoriteStocks().getSymbols();

            System.out.println("⏰ Čas " + nowFormatted + " => Spouštím pro oblíbené akcie: " + favoriteSymbols);

            for (String symbol : favoriteSymbols) {
                List<HistoricalData> data = burzaService.fetchHistoricalData(symbol);
                List<HistoricalData> filtered = burzaService.filterDataDown(data);

                System.out.println("📉 " + symbol + ": nalezeno " + filtered.size() + " poklesů");
                // Zde můžeš uložit, zpracovat nebo poslat data dál
            }
        }
    }
}
