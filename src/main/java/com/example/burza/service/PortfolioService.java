package com.example.burza.service;

import com.example.burza.model.DailyData;
import com.example.burza.model.Portfolio;
import com.example.burza.model.TradeOrder;
import com.example.burza.model.TradeResult;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service handling portfolio operations.
 * Manages user portfolios and executes trading transactions.
 */
@Service
@Data
public class PortfolioService {
    private final Portfolio portfolio;
    private final StockService stockService;

    /**
     * Constructor initializing portfolio and stock service.
     *
     * @param stockService service for fetching stock data
     */
    @Autowired
    public PortfolioService(StockService stockService) {
        this.stockService = stockService;
        this.portfolio = new Portfolio();
    }

    /**
     * Executes a trade order based on current stock prices.
     *
     * @param order the trade order
     * @return the result of the trade execution
     */
    public TradeResult executeTrade(TradeOrder order) {
        if (order.getOrderType() == null) {
            return new TradeResult(false, "Invalid trade order type", 0, 0, portfolio.getBalance());
        }

        List<DailyData> dailyData = stockService.fetchDailyTimeSeries(order.getSymbol());
        if (dailyData.isEmpty()) {
            return new TradeResult(false, "Impossible to get the current value of the stock", 0, 0, portfolio.getBalance());
        }

        double currentPrice = dailyData.get(0).getClose();

        TradeResult result;
        if (order.getOrderType() == TradeOrder.OrderType.BUY) {
            result = executeBuy(order, currentPrice);
        } else {
            result = executeSell(order, currentPrice);
        }

        savePortfolioState();
        return result;
    }

    private TradeResult executeBuy(TradeOrder order, double currentPrice) {
        double totalCost = currentPrice * order.getQuantity();
        if (totalCost > portfolio.getBalance()) {
            return new TradeResult(false, "Not enough resources for this purchase", currentPrice, totalCost, portfolio.getBalance());
        }

        int currentQuantity = portfolio.getHoldings().getOrDefault(order.getSymbol(), 0);
        portfolio.getHoldings().put(order.getSymbol(), currentQuantity + order.getQuantity());
        portfolio.setBalance(portfolio.getBalance() - totalCost);

        // ✅ Uložit změnu
        savePortfolioState();

        return new TradeResult(true, "Purchase successful", currentPrice, totalCost, portfolio.getBalance());
    }

    private TradeResult executeSell(TradeOrder order, double currentPrice) {
        int currentQuantity = portfolio.getHoldings().getOrDefault(order.getSymbol(), 0);
        if (currentQuantity < order.getQuantity()) {
            return new TradeResult(false, "Not enough stocks for the purchase", currentPrice, 0, portfolio.getBalance());
        }

        double totalValue = currentPrice * order.getQuantity();
        int newQuantity = currentQuantity - order.getQuantity();

        if (newQuantity > 0) {
            portfolio.getHoldings().put(order.getSymbol(), newQuantity);
        } else {
            portfolio.getHoldings().remove(order.getSymbol()); // ✅ Odstranění akcie s hodnotou 0
        }

        portfolio.setBalance(portfolio.getBalance() + totalValue);

        // ✅ Uložit změnu
        savePortfolioState();

        return new TradeResult(true, "Sale successful", currentPrice, totalValue, portfolio.getBalance());
    }

    // ✅ Přidána metoda pro uložení změn v portfoliu
    private void savePortfolioState() {
        System.out.println("Saving portfolio: " + portfolio);
    }

}