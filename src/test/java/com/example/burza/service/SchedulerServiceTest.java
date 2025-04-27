package com.example.burza.service;

import com.example.burza.model.DaysDeclineFilter;
import com.example.burza.model.FavoriteStocks;
import com.example.burza.model.Portfolio;
import com.example.burza.model.Symbol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalTime;
import java.util.List;

import static org.mockito.Mockito.*;

class SchedulerServiceTest {

    @Mock
    private BurzaService burzaService;

    @Mock
    private PortfolioService portfolioService;

    @Mock
    private Portfolio portfolio;

    @Mock
    private FavoriteStocks favoriteStocks;

    @Mock
    private DaysDeclineFilter lastThreeDaysDeclineFilter;

    @Mock
    private LoggingService loggingService;

    @InjectMocks
    private SchedulerService schedulerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Přímé nastavení "fetchTimes" na aktuální čas (aby se spustil cron)
        try {
            var field = SchedulerService.class.getDeclaredField("fetchTimes");
            field.setAccessible(true);
            field.set(schedulerService, LocalTime.now().withSecond(0).withNano(0).format(java.time.format.DateTimeFormatter.ofPattern("H:mm")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(portfolioService.getPortfolio()).thenReturn(portfolio);
        when(portfolio.getFavoriteStocks()).thenReturn(favoriteStocks);
        when(favoriteStocks.getSymbols()).thenReturn(List.of(new Symbol("AAPL", "Apple")));
    }

    @Test
    void testRunFilterForFavoriteStocks_whenTimeMatches_andSymbolsPassFilter() throws InterruptedException {
        // Simuluj že filtr vrátí nějaké symboly
        when(lastThreeDaysDeclineFilter.filter(anyList(), anyInt())).thenReturn(List.of(new Symbol("AAPL", "Apple")));

        schedulerService.runFilterForFavoriteStocks();

        verify(portfolioService, times(1)).getPortfolio();
        verify(portfolio, times(1)).getFavoriteStocks();
        verify(favoriteStocks, times(1)).getSymbols();
        verify(lastThreeDaysDeclineFilter, times(1)).filter(anyList(), anyInt());
        verify(portfolioService, times(1)).transaction(anyList());
        verify(loggingService, atLeastOnce()).log(anyString());
    }

    @Test
    void testRunFilterForFavoriteStocks_whenTimeMatches_butNoSymbolsPassFilter() throws InterruptedException {
        // Simuluj že filtr nic nevrátí
        when(lastThreeDaysDeclineFilter.filter(anyList(), anyInt())).thenReturn(List.of());

        schedulerService.runFilterForFavoriteStocks();

        verify(portfolioService, times(1)).getPortfolio();
        verify(portfolio, times(1)).getFavoriteStocks();
        verify(favoriteStocks, times(1)).getSymbols();
        verify(lastThreeDaysDeclineFilter, times(1)).filter(anyList(), anyInt());
        verify(portfolioService, never()).transaction(anyList()); // Nemá se volat transaction
        verify(loggingService, atLeastOnce()).log(contains("No symbols passed"));
    }

    @Test
    void testRunFilterForFavoriteStocks_whenTimeDoesNotMatch() throws InterruptedException {
        // Nastavíme fetchTimes na úplně jiný čas
        try {
            var field = SchedulerService.class.getDeclaredField("fetchTimes");
            field.setAccessible(true);
            field.set(schedulerService, "00:00");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        schedulerService.runFilterForFavoriteStocks();

        verify(portfolioService, never()).getPortfolio();
        verify(lastThreeDaysDeclineFilter, never()).filter(anyList(), anyInt());
        verify(portfolioService, never()).transaction(anyList());
    }

    @Test
    void testRunFilterForFavoriteStocks_ExceptionHandling() throws InterruptedException {
        // Simulujeme výjimku při získávání portfolia
        when(portfolioService.getPortfolio()).thenThrow(new RuntimeException("Simulated error"));

        schedulerService.runFilterForFavoriteStocks();

        // Ověříme že žádné další metody nebyly volané
        verify(favoriteStocks, never()).getSymbols();
        verify(lastThreeDaysDeclineFilter, never()).filter(anyList(), anyInt());
        verify(portfolioService, never()).transaction(anyList());
    }
}
