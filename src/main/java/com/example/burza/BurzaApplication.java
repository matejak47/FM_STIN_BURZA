package com.example.burza;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Main application class for the Burza (Stock Exchange) application.
 * Initializes Spring Boot application and configures necessary beans.
 */
@SpringBootApplication
public class BurzaApplication {

	/**
	 * Main entry point of the application.
	 * @param args Command line arguments passed to the application
	 */
	public static void main(String[] args) {

		SpringApplication.run(BurzaApplication.class, args);
	}

	/**
	 * Creates and configures a RestTemplate bean for making HTTP requests.
	 * @return Configured RestTemplate instance
	 */
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
