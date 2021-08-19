package com.noagility.personalcrm;

import com.noagility.personalcrm.service.TestClassService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class PersonalCRMApplication {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	public static void main(String[] args) {
		SpringApplication.run(PersonalCRMApplication.class, args);
	}

	@Bean
	TestClassService getTestClassServiceBean() {
		return new TestClassService();
	}

	@Bean
	CommandLineRunner runner() {
		return args -> { LOGGER.info("Application has started."); };
	}

}