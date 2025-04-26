package com.example.burza.service;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Data
@Service
public class LoggingService {
    private final List<String> logs = new LinkedList<>();

    public void log(String message) {
        if (logs.size() >= 500) { // třeba udržuj jen posledních 500 zpráv
            logs.remove(0);
        }
        logs.add(message);
    }
}
