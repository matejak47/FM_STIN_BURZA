package com.example.burza.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DailyDataTest {

    @Test
    void testConstructorAndGetters() {
        DailyData dailyData = new DailyData("2024-02-24", 100.5, 110.0, 98.0, 105.0, 50000);

        assertEquals("2024-02-24", dailyData.getDate());
        assertEquals(100.5, dailyData.getOpen());
        assertEquals(110.0, dailyData.getHigh());
        assertEquals(98.0, dailyData.getLow());
        assertEquals(105.0, dailyData.getClose());
        assertEquals(50000, dailyData.getVolume());
    }

    @Test
    void testSetters() {
        DailyData dailyData = new DailyData("2024-02-24", 100.5, 110.0, 98.0, 105.0, 50000);

        dailyData.setDate("2024-02-25");
        dailyData.setOpen(101.0);
        dailyData.setHigh(111.0);
        dailyData.setLow(99.0);
        dailyData.setClose(106.0);
        dailyData.setVolume(55000);

        assertEquals("2024-02-25", dailyData.getDate());
        assertEquals(101.0, dailyData.getOpen());
        assertEquals(111.0, dailyData.getHigh());
        assertEquals(99.0, dailyData.getLow());
        assertEquals(106.0, dailyData.getClose());
        assertEquals(55000, dailyData.getVolume());
    }

    @Test
    void testEqualsAndHashCode() {
        DailyData data1 = new DailyData("2024-02-24", 100.5, 110.0, 98.0, 105.0, 50000);
        DailyData data2 = new DailyData("2024-02-24", 100.5, 110.0, 98.0, 105.0, 50000);
        DailyData data3 = new DailyData("2024-02-25", 101.0, 111.0, 99.0, 106.0, 55000);

        assertEquals(data1, data2);
        assertNotEquals(data1, data3);
        assertEquals(data1.hashCode(), data2.hashCode());
        assertNotEquals(data1.hashCode(), data3.hashCode());
    }

    @Test
    void testToString() {
        DailyData dailyData = new DailyData("2024-02-24", 100.5, 110.0, 98.0, 105.0, 50000);
        String expectedString = "DailyData(date=2024-02-24, open=100.5, high=110.0, low=98.0, close=105.0, volume=50000)";
        assertEquals(expectedString, dailyData.toString());
    }
}
