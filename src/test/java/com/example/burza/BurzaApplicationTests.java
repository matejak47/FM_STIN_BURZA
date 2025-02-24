package com.example.burza;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

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
	void applicationStartsWithoutErrors() {
		assertTrue(applicationContext.containsBean("burzaApplication"));
	}

	@Test
	void mainMethodExecutesWithoutException() {
		assertDoesNotThrow(() -> BurzaApplication.main(new String[]{}));
	}
}