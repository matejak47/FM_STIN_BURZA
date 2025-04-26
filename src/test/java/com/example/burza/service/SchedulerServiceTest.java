package com.example.burza.service;

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

    @InjectMocks
    private SchedulerService schedulerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Přímé nastavení "fetchTimes" pomocí reflexe
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
    void testRunFilterForFavoriteStocks_whenTimeMatches() {
        schedulerService.runFilterForFavoriteStocks();

        verify(portfolioService, times(1)).getPortfolio();
        verify(portfolio, times(1)).getFavoriteStocks();
        verify(favoriteStocks, times(1)).getSymbols();
    }

    @Test
    void testRunFilterForFavoriteStocks_whenTimeDoesNotMatch() {
        // Nastavíme fetchTimes na jiný čas
        try {
            var field = SchedulerService.class.getDeclaredField("fetchTimes");
            field.setAccessible(true);
            field.set(schedulerService, "00:00"); // Něco úplně jiného
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        schedulerService.runFilterForFavoriteStocks();

        // PortfolioService by se NEMĚLO zavolat
        verify(portfolioService, never()).getPortfolio();
    }
}
