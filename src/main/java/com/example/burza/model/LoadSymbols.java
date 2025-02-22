package com.example.burza.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Data
@Service
public class LoadSymbols {
    private String path = "data/symbols.json";


        public List<Symbol> LoadSymbols () throws IOException {
            ClassPathResource resource = new ClassPathResource(path);
            ObjectMapper objectMapper = new ObjectMapper();

            // Použití InputStream pro načítání z resource
            try (InputStream inputStream = resource.getInputStream()) {
                Symbols symbols = objectMapper.readValue(inputStream, Symbols.class);
                return symbols.getSymbols();
            }
        }
    }