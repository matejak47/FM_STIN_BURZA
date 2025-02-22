package com.example.burza.model;

import lombok.Data;
import java.util.Map;

/**
 * Data model representing a user's portfolio.
 */
@Data
public class Portfolio {
    private String userId;
    // Map k uchování symbolu a počtu kusů
    private Map<String, Integer> holdings;
}
