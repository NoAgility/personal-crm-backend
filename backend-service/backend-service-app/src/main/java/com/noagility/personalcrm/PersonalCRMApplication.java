package com.noagility.personalcrm;

import com.noagility.personalcrm.mapper.*;
import com.noagility.personalcrm.service.AccountService;
import com.noagility.personalcrm.service.ChatService;
import com.noagility.personalcrm.service.ContactService;
import com.noagility.personalcrm.service.MeetingService;
import com.noagility.personalcrm.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
public class PersonalCRMApplication {

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
	IntegerRowMapper getIntegerRowMapper(){
		return new IntegerRowMapper();
	}
	@Bean
	TaskService getTaskService() { return new TaskService(); }
	@Bean
	TaskRowMapper TaskClassRowMapper() { return new TaskRowMapper(); }
	@Bean
	TaskContactRowMapper TaskContactClassRowMapper() { return new TaskContactRowMapper(); }
	@Bean
	TaskNoteRowMapper TaskNoteClassRowMapper() { return new TaskNoteRowMapper(); }
	@Bean
	MeetingRowMapper MeetingRowMapper(){
		return new MeetingRowMapper();
	}

	@Bean
	MinuteRowMapper MinutegRowMapper(){
		return new MinuteRowMapper();
	}

	@Bean
	MeetingService MeetingService(){
		return new MeetingService();
	}
	
	@Bean
	CommandLineRunner runner() {
		return args -> {
			log.info("   _  __     ___       _ ___ __         ___                             __  _____        \n" +
					"  / |/ /__  / _ |___ _(_) (_) /___ __  / _ \\___ _______ ___  ___  ___ _/ / / ___/_____ _ \n" +
					" /    / _ \\/ __ / _ `/ / / / __/ // / / ___/ -_) __(_-</ _ \\/ _ \\/ _ `/ / / /__/ __/  ' \\\n" +
					"/_/|_/\\___/_/ |_\\_, /_/_/_/\\__/\\_, / /_/   \\__/_/ /___/\\___/_//_/\\_,_/_/  \\___/_/ /_/_/_/\n" +
					"               /___/          /___/                                                      ");
			log.info("NoAgility Personal CRM Backend Application has started");
		};
	}

}