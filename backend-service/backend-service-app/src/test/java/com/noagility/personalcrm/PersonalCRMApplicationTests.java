package com.noagility.personalcrm;

import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import net.bytebuddy.agent.VirtualMachine.ForHotSpot.Connection.Response;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noagility.personalcrm.deserializer.AccountDeserializer;
import com.noagility.personalcrm.mapper.AccountRowMapper;
import com.noagility.personalcrm.model.Account;

@SpringBootTest
@AutoConfigureMockMvc
@ConfigurationProperties("application.properties")
@TestPropertySource(locations = "/application-test.properties")
class PersonalCRMApplicationTests {

	@Autowired
	MockMvc mvc;
	@Autowired
	AccountDeserializer accountDeserializer;

	@Test
	public void testTestCase() throws Exception {
		/*
		mvc.perform(get("/account/dburl")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk());
				
		 */
	}

	@Test
	public void testAccountCreation() throws Exception{
		Account acc = new Account(1, "testAccountCreation", "testingname", LocalDate.of(2000, 1, 2), LocalDate.now());
		String jsonCreate = new StringBuilder()
			.append("{")
			.append("'username': 'testAccountCreation'")
			.append(", 'password': 'testingpassword'")
			.append(", 'name': 'testingname'")
			.append(", 'dob': '2000-01-02'")
			.append("}")
			.toString().replaceAll("'", "\"");

		mvc.perform(post("/account/create")
			.contentType(MediaType.APPLICATION_JSON)
			.content(jsonCreate)
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk());
		
		String returnedJson = mvc.perform(get("/account/get?username=testAccountCreation")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		Account returnedAccount = accountDeserializer.deserializeAccount(returnedJson);
		acc.setAccountID(returnedAccount.getAccountID());
		acc.setAccountCreation(returnedAccount.getAccountCreation());

		assert(acc.equals(returnedAccount));
	}

	@Test
	public void testAccountDeactivation() throws Exception{
		String jsonCreate = new StringBuilder()
			.append("{")
			.append("'username': 'testAccountDeactivation'")
			.append(", 'password': 'testingpassword'")
			.append(", 'name': 'testingname'")
			.append(", 'dob': '2000-01-02'")
			.append("}")
			.toString().replaceAll("'", "\"");

		mvc.perform(post("/account/create")
			.contentType(MediaType.APPLICATION_JSON)
			.content(jsonCreate)
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk());
		
		String returnedJson = mvc.perform(get("/account/get?username=testAccountDeactivation")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print())
			.andReturn()
			.getResponse()
			.getContentAsString();

		Account beforeDeactivation = accountDeserializer.deserializeAccount(returnedJson);

		String jsonDeactivate = new StringBuilder()
			.append("{")
			.append("'id': " + beforeDeactivation.getAccountID())
			.append("}")
			.toString().replaceAll("'", "\"");
		
		mvc.perform(post("/account/deactivate")
			.contentType(MediaType.APPLICATION_JSON)
			.content(jsonDeactivate)
			.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk());

		returnedJson = mvc.perform(get("/account/get?username=testAccountDeactivation")
			.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print())
			.andReturn()
			.getResponse()
			.getContentAsString();
		
		Account afterDeactivation = accountDeserializer.deserializeAccount(returnedJson);
		afterDeactivation.setAccountActive(!afterDeactivation.isAccountActive());

		assert(beforeDeactivation.equals(afterDeactivation));
	}

}