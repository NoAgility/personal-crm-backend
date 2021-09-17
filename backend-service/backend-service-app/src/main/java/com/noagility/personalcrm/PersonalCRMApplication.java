package com.noagility.personalcrm;

import com.noagility.personalcrm.mapper.*;
import com.noagility.personalcrm.service.AccountService;
import com.noagility.personalcrm.service.ChatService;
import com.noagility.personalcrm.service.ContactService;
import com.noagility.personalcrm.service.JwtUserDetailsService;
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
	AccountRowMapper AccountClassRowMapper() {
		return new AccountRowMapper();
	}
	@Bean
	AccountService getAccountService() {
		return new AccountService();
	}
	@Bean
	LoginRowMapper LoginClassRowMapper() {return new LoginRowMapper();}
	@Bean
	ContactService getContactService() {
		return new ContactService();
	}
	@Bean
	ContactRowMapper ContactClassRowMapper() {return new ContactRowMapper();}

	@Bean
	ChatService getChatService(){
		return new ChatService();
	}

	@Bean 
	ChatRowMapper getChatRowMapper(){
		return new ChatRowMapper();
	}

	@Bean 
	MessageRowMapper getMessageRowMapper(){
		return new MessageRowMapper();
	}

	@Bean
	CommandLineRunner runner() {
		return args -> { LOGGER.info("Application has started.");
		System.out.println("\n" + System.getenv("SPRING_DATASOURCE_URL")); };
	}

}