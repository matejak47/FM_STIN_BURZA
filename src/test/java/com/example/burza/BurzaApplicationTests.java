package com.example.burza;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BurzaApplication to verify Spring context and critical beans.
 */
@SpringBootTest
class BurzaApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        // Test to ensure the Spring application context loads successfully
        assertNotNull(applicationContext, "Application context should not be null");
    }

    @Test
    void restTemplateBeanExists() {
        // Test to ensure RestTemplate bean is available in the application context
        RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
        assertNotNull(restTemplate, "RestTemplate bean should exist");
    }

    @Test
    void mainMethodExecutesWithoutException() {
        // Test to ensure that the application main method runs without throwing exceptions
        assertDoesNotThrow(() -> BurzaApplication.main(new String[]{}),
                "Application main method should execute without exceptions");
    }
}
