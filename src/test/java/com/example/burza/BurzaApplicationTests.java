package com.example.burza;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for BurzaApplication to verify Spring context and critical beans.
 */
@SpringBootTest
class BurzaApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertNotNull(applicationContext);
    }

    @Test
    void restTemplateBeanExists() {
        RestTemplate restTemplate = applicationContext.getBean(RestTemplate.class);
        assertNotNull(restTemplate);
    }

    @Test
    void mainMethodExecutesWithoutException() {
        assertDoesNotThrow(() -> BurzaApplication.main(new String[]{}));
    }
}
