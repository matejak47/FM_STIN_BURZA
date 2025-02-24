package com.example.burza.controller;

import com.example.burza.service.BurzaService;
import com.example.burza.service.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BurzaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BurzaService burzaService;

    @Mock
    private StockService stockService;

    @InjectMocks
    private BurzaController burzaController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(burzaController).build();
    }

    @Test
    void testGetHistoricalData() throws Exception {
        mockMvc.perform(get("/api/burza/historical?symbol=AAPL"))
                .andExpect(status().isOk());
    }
}
