package com.example.burza.controller;

import com.example.burza.service.LoggingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final LoggingService loggingService;

    public LogController(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    @GetMapping
    public List<String> getLogs() {
        return loggingService.getLogs();
    }
}
