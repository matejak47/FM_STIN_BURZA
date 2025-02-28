package com.example.burza.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Service class for loading stock symbols from a JSON file.
 */
@Data
@Service
public class SymbolLoading {
    private final ObjectMapper objectMapper;
    private final ClassPathResource resource;

    public SymbolLoading(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.resource = new ClassPathResource("data/symbols.json");
    }

    /**
     * Loads stock symbols from a JSON file.
     *
     * @return List of stock symbols
     * @throws IOException If there's an error reading the JSON file
     */
    public List<Symbol> LoadSymbols() throws IOException {
        try (InputStream inputStream = resource.getInputStream()) {
            Symbols symbols = objectMapper.readValue(inputStream, Symbols.class);
            return symbols.getSymbols();
        }
    }
}
