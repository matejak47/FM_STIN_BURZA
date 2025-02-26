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
        List<DailyData> dailyData = stockService.fetchDailyTimeSeries(order.getSymbol());
        if (dailyData.isEmpty()) {
            return new TradeResult(false, "Impossible to get the current value of the stock", 0, 0, portfolio.getBalance());
        }

        double currentPrice = dailyData.get(0).getClose();

        if (order.getOrderType() == TradeOrder.OrderType.BUY) {
            return executeBuy(order, currentPrice);
        } else {
            return executeSell(order, currentPrice);
        }
    }

    /**
     * Handles buying stocks.
     *
     * @param order        the trade order
     * @param currentPrice the current stock price
     * @return the result of the buy operation
     */
    private TradeResult executeBuy(TradeOrder order, double currentPrice) {
        double totalCost = currentPrice * order.getQuantity();
        if (totalCost > portfolio.getBalance()) {
            return new TradeResult(
                    false,
                    "Not enough resources for this purchase",
                    currentPrice,
                    totalCost,
                    portfolio.getBalance()
            );
        }

        int currentQuantity = portfolio.getHoldings().getOrDefault(order.getSymbol(), 0);
        portfolio.getHoldings().put(order.getSymbol(), currentQuantity + order.getQuantity());
        portfolio.setBalance(portfolio.getBalance() - totalCost);

        return new TradeResult(
                true,
                "Purchase successful",
                currentPrice,
                totalCost,
                portfolio.getBalance()
        );
    }

    /**
     * Handles selling stocks.
     *
     * @param order        the trade order
     * @param currentPrice the current stock price
     * @return the result of the sell operation
     */
    private TradeResult executeSell(TradeOrder order, double currentPrice) {
        int currentQuantity = portfolio.getHoldings().getOrDefault(order.getSymbol(), 0);

        if (currentQuantity < order.getQuantity()) {
            return new TradeResult(
                    false,
                    "Not enough stocks for the purchase",
                    currentPrice,
                    0,
                    portfolio.getBalance()
            );
        }

        double totalValue = currentPrice * order.getQuantity();

        portfolio.getHoldings().put(
                order.getSymbol(),
                currentQuantity - order.getQuantity()
        );
        portfolio.setBalance(portfolio.getBalance() + totalValue);

        return new TradeResult(
                true,
                "Purchase successful",
                currentPrice,
                totalValue,
                portfolio.getBalance()
        );
    }

}