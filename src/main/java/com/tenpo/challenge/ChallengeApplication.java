package com.tenpo.challenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class for the Tenpo Challenge application.
 * This class is responsible for bootstrapping the Spring Boot application.
 * It contains the main method which serves as the entry point for the application.
 *
 * @author bbeltran
 * @version 1.0
 * @since 2023-10-05
 */
@SpringBootApplication
public class ChallengeApplication {

	/**
	 * The main method which serves as the entry point for the Spring Boot application.
	 *
	 * @param args command-line arguments passed to the application.
	 */
	public static void main(String[] args) {
		SpringApplication.run(ChallengeApplication.class, args);
	}

}
