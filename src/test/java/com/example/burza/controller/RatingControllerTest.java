package com.example.burza.controller;

import com.example.burza.service.PortfolioService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RatingControllerTest {

    @Test
    void triggerTransaction_ShouldCallTransactionAndReturnOk() throws Exception {
        PortfolioService portfolioService = Mockito.mock(PortfolioService.class);

        RatingController ratingController = new RatingController(portfolioService);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(ratingController).build();

        mockMvc.perform(post("/api/rating")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(portfolioService, Mockito.times(1)).transaction();
    }
}
