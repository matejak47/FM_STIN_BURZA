package com.example.burza.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TradeOrderTest {

    @Test
    public void testAllArgsConstructor() {
        // Create a TradeOrder instance using the AllArgsConstructor
        TradeOrder order = new TradeOrder("AAPL", TradeOrder.OrderType.BUY, 100);

        // Assert the fields are correctly set
        assertEquals("AAPL", order.getSymbol());
        assertEquals(TradeOrder.OrderType.BUY, order.getOrderType());
        assertEquals(100, order.getQuantity());
    }

    @Test
    public void testNoArgsConstructor() {
        // Create a TradeOrder instance using the NoArgsConstructor
        TradeOrder order = new TradeOrder();

        // Assert the fields are initialized with default values
        assertNull(order.getSymbol());
        assertNull(order.getOrderType());
        assertEquals(0, order.getQuantity());
    }

    @Test
    public void testSetterAndGetter() {
        // Create an instance using the NoArgsConstructor
        TradeOrder order = new TradeOrder();

        // Set values using setters
        order.setSymbol("GOOG");
        order.setOrderType(TradeOrder.OrderType.SELL);
        order.setQuantity(50);

        // Assert the values are correctly set
        assertEquals("GOOG", order.getSymbol());
        assertEquals(TradeOrder.OrderType.SELL, order.getOrderType());
        assertEquals(50, order.getQuantity());
    }

    @Test
    public void testEnumOrderType() {
        // Test the enum values
        assertEquals(TradeOrder.OrderType.BUY, TradeOrder.OrderType.valueOf("BUY"));
        assertEquals(TradeOrder.OrderType.SELL, TradeOrder.OrderType.valueOf("SELL"));
    }

    @Test
    public void testToStringMethod() {
        // Create a TradeOrder instance
        TradeOrder order = new TradeOrder("MSFT", TradeOrder.OrderType.BUY, 200);

        // Test the toString method generated by Lombok's @Data annotation
        String expectedString = "TradeOrder(symbol=MSFT, orderType=BUY, quantity=200)";
        assertEquals(expectedString, order.toString());
    }
}
