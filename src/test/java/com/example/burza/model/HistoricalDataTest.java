package com.example.burza.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HistoricalDataTest {

    @Test
    void testConstructorAndGetters() {
        HistoricalData historicalData = new HistoricalData("2024-02-24", 100.5, 105.0);

        assertEquals("2024-02-24", historicalData.getDate());
        assertEquals(100.5, historicalData.getOpenPrice());
        assertEquals(105.0, historicalData.getClosePrice());
    }

    @Test
    void testSetters() {
        HistoricalData historicalData = new HistoricalData("2024-02-24", 100.5, 105.0);

        historicalData.setDate("2024-02-25");
        historicalData.setOpenPrice(101.0);
        historicalData.setClosePrice(106.0);

        assertEquals("2024-02-25", historicalData.getDate());
        assertEquals(101.0, historicalData.getOpenPrice());
        assertEquals(106.0, historicalData.getClosePrice());
    }

    @Test
    void testEqualsAndHashCode() {
        HistoricalData data1 = new HistoricalData("2024-02-24", 100.5, 105.0);
        HistoricalData data2 = new HistoricalData("2024-02-24", 100.5, 105.0);
        HistoricalData data3 = new HistoricalData("2024-02-25", 101.0, 106.0);

        assertEquals(data1, data2);
        assertNotEquals(data1, data3);
        assertEquals(data1.hashCode(), data2.hashCode());
        assertNotEquals(data1.hashCode(), data3.hashCode());
    }

    @Test
    void testToString() {
        HistoricalData historicalData = new HistoricalData("2024-02-24", 100.5, 105.0);
        String expectedString = "HistoricalData(date=2024-02-24, openPrice=100.5, closePrice=105.0)";
        assertEquals(expectedString, historicalData.toString());
    }
}
