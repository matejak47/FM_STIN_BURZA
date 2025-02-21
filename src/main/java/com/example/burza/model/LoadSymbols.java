package com.example.burza.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Data
public class LoadSymbols {
    private String path = "data/symbols.json";


        public List<Symbol> LoadSymbols () throws IOException {

            ClassPathResource resource = new ClassPathResource(path);
            ObjectMapper objectMapper = new ObjectMapper();

            try (InputStream inputStream = resource.getInputStream()) {

                Symbols symbols = objectMapper.readValue(inputStream, Symbols.class);
                return symbols.getSymbols();
            }
        }
    }