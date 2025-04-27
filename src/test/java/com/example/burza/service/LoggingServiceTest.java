package com.example.burza.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoggingServiceTest {

    private LoggingService loggingService;

    @BeforeEach
    void setUp() {
        loggingService = new LoggingService();
    }

    @Test
    void testLogAddsMessage() {
        loggingService.log("Test message");
        assertEquals(1, loggingService.getLogs().size());
        assertEquals("Test message", loggingService.getLogs().get(0));
    }

    @Test
    void testLogMaintainsMaxSize() {
        // Přidáme 501 zpráv
        for (int i = 0; i < 501; i++) {
            loggingService.log("Message " + i);
        }
        assertEquals(500, loggingService.getLogs().size());
        assertEquals("Message 1", loggingService.getLogs().get(0)); // první by měla být Message 1
    }
}
