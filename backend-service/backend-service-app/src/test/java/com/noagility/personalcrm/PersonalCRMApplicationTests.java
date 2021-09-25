package com.noagility.personalcrm;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.noagility.personalcrm.deserializer.ContactDeserializer;
import com.noagility.personalcrm.model.Contact;
import org.json.JSONObject;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noagility.personalcrm.deserializer.AccountDeserializer;
import com.noagility.personalcrm.mapper.AccountRowMapper;
import com.noagility.personalcrm.model.Account;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.Cookie;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "/application-test.properties")
class PersonalCRMApplicationTests {

	@Autowired
	MockMvc mvc;
	@Autowired
	AccountDeserializer accountDeserializer;
	@Autowired
	ContactDeserializer contactDeserializer;

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

		MvcResult result = mvc.perform(post("/authenticate/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\": \"testAccountDeactivation\", \"password\":\"testingpassword\"}")
						.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		Cookie cookie = result.getResponse().getCookie("jwt");

		String returnedJson = mvc.perform(MockMvcRequestBuilders.get("/account/get?username=testAccountDeactivation")
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

		mvc.perform(MockMvcRequestBuilders.post("/account/deactivate")
						.cookie(cookie)
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

	@Test
	public void testContactCreationRead() throws Exception{

		String jsonCreate = new StringBuilder()
				.append("{")
				.append("'username': 'testContactCreation'")
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

		String jsonCreate2 = new StringBuilder()
				.append("{")
				.append("'username': 'testContactCreation2'")
				.append(", 'password': 'testingpassword2'")
				.append(", 'name': 'testingname2'")
				.append(", 'dob': '2000-01-02'")
				.append("}")
				.toString().replaceAll("'", "\"");

		mvc.perform(post("/account/create")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonCreate2)
						.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk());

		MvcResult result = mvc.perform(post("/authenticate/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\": \"testContactCreation\", \"password\":\"testingpassword\"}")
						.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andReturn();

		Cookie cookie = result.getResponse().getCookie("jwt");

		String jsonContactCreate = new StringBuilder()
				.append("{")
				.append("'contact': 'testContactCreation2'")
				.append("}")
				.toString().replaceAll("'", "\"");

		mvc.perform(post("/contact/create")
						.cookie(cookie)
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonContactCreate)
						.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk());

		mvc.perform(get("/contact/read")
						.cookie(cookie))
				.andExpect(content().json(String.format("[{\"contactID\":4,\"contactCreatedOn\":\"%s\"}]",java.time.LocalDate.now())));
	}

}