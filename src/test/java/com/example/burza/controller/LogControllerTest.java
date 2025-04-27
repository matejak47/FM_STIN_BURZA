package com.example.burza.controller;

import com.example.burza.service.LoggingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class LogControllerTest {

    private LogController logController;
    private LoggingService loggingService;

    @BeforeEach
    void setUp() {
        loggingService = mock(LoggingService.class); //
        logController = new LogController(loggingService); //
    }

    @Test
    void testGetLogs() {
        when(loggingService.getLogs()).thenReturn(List.of("Log 1", "Log 2"));

        List<String> logs = logController.getLogs();

        assertEquals(2, logs.size());
        assertEquals("Log 1", logs.get(0));
        assertEquals("Log 2", logs.get(1));
        verify(loggingService, times(1)).getLogs(); // ✅ ověříme že se volalo
    }
}
