package com.noagility.personalcrm;

import com.noagility.personalcrm.deserializer.AccountDeserializer;
import com.noagility.personalcrm.deserializer.ContactDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "/application-test.properties")
public class TasksTesting {

    @Autowired
    MockMvc mvc;
    @Autowired
    AccountDeserializer accountDeserializer;
    @Autowired
    ContactDeserializer contactDeserializer;

    // Basic API Testing first before performance testing.



    // to run faster or to test more performance change the number of accounts created.
    private int numberOfAccounts = 21;
    private int numberOfAccountsWithTasks = 11;
    private int largeNumberOfTasks = 201;

    // create numberOfAccounts test accounts which will be used in the integration testing for adding to tasks.
    // Have all users add each other as contacts for later.
    @Test
    public void createUsers() throws Exception {
        for(int i = 1; i < numberOfAccounts; i++){
            String jsonCreate = new StringBuilder()
                    .append("{")
                    .append("'username': 'taskAccount")
                    .append(String.valueOf(i) + "'")
                    .append(", 'password': 'password'")
                    .append(", 'name': '")
                    .append(String.valueOf(i) + "'")
                    .append(", 'dob': '2000-01-02'")
                    .append("}")
                    .toString().replaceAll("'", "\"");

            mvc.perform(post("/account/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonCreate)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
        for(int i = 1; i < numberOfAccounts; i++){
            String loginContent = "{\"username\": \"taskAccount"+ String.valueOf(i) + "\", \"password\":\"password\"}";
            MvcResult result = mvc.perform(post("/authenticate/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginContent)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            Cookie cookie = result.getResponse().getCookie("jwt");

            for(int j = 1; j < numberOfAccountsWithTasks; j++){
                if(i == j){
                    continue;
                }
                String jsonContactCreate = new StringBuilder()
                        .append("{")
                        .append("'contact': 'taskAccount")
                        .append(String.valueOf(j) + "'")
                        .append("}")
                        .toString().replaceAll("'", "\"");

                mvc.perform(post("/contact/create")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContactCreate)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());

            }
        }
    }


    // first performance testing will create a large number of tasks for 1 user (no Deadline, priority, contacts or notes)
    @Test
    public void basePerformanceTest1() throws Exception {
        String loginContent = "{\"username\": \"taskAccount1\", \"password\":\"password\"}";
        MvcResult result = mvc.perform(post("/authenticate/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginContent)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Cookie cookie = result.getResponse().getCookie("jwt");

        for (int j = 1; j < largeNumberOfTasks; j++) {

            // create the tasks for each account.
            String taskContent = "{\n" +
                    "    \"contactIDs\":[],\n" +
                    "    \"taskNotes\": [],\n" +
                    "    \"taskName\": \"TaskNumber" + String.valueOf(j) + "\"\n" +
                    "}";

            mvc.perform(post("/task/createTask")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(taskContent)
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        // go through and delete the tasks created.
        for (int j = 1; j < largeNumberOfTasks; j++) {
            // create the tasks for each account.
            String taskContent = "{\n" +
                    "    \"taskID\": " + String.valueOf(j) + "\n" +
                    "}";

            mvc.perform(post("/task/deleteTask")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(taskContent)
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    // the second performance test will create a large number of tasks for 1 user with deadline
    @Test
    public void basePerformanceTest2() throws Exception {
        String loginContent = "{\"username\": \"taskAccount1\", \"password\":\"password\"}";
        MvcResult result = mvc.perform(post("/authenticate/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginContent)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Cookie cookie = result.getResponse().getCookie("jwt");

        for (int j = 1; j < largeNumberOfTasks; j++) {

            // create the tasks for each account.
            String taskContent = "{\n" +
                    "    \"contactIDs\":[],\n" +
                    "    \"taskNotes\": [],\n" +
                    "    \"taskName\": \"TaskNumber" + String.valueOf(j) + "\",\n" +
                    "    \"priority\": 3\n" +
                    "}";

            mvc.perform(post("/task/createTask")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(taskContent)
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        // go through and delete the tasks created.
        for (int j = 1; j < largeNumberOfTasks; j++) {
            // create the tasks for each account.
            String taskContent = "{\n" +
                    "    \"taskID\": " + String.valueOf(j) + "\n" +
                    "}";

            mvc.perform(post("/task/deleteTask")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(taskContent)
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    // the 3rd performance test will create a large number of tasks for 1 user with deadline
    @Test
    public void basePerformanceTest3() throws Exception {
        String loginContent = "{\"username\": \"taskAccount1\", \"password\":\"password\"}";
        MvcResult result = mvc.perform(post("/authenticate/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginContent)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Cookie cookie = result.getResponse().getCookie("jwt");

        for (int j = 1; j < largeNumberOfTasks; j++) {

            // create the tasks for each account.
            String taskContent = "{\n" +
                    "    \"contactIDs\":[],\n" +
                    "    \"taskNotes\": [],\n" +
                    "    \"taskName\": \"TaskNumber" + String.valueOf(j) + "\",\n" +
                    "    \"deadline\": \"2021-11-5 04:00:00\"" +
                    "}";

            mvc.perform(post("/task/createTask")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(taskContent)
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        // go through and delete the tasks created.
        for (int j = 1; j < largeNumberOfTasks; j++) {
            // create the tasks for each account.
            String taskContent = "{\n" +
                    "    \"taskID\": " + String.valueOf(j) + "\n" +
                    "}";

            mvc.perform(post("/task/deleteTask")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(taskContent)
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    // the 4th performance test will create a large number of tasks for 1 user with deadline and priority
    @Test
    public void basePerformanceTest4() throws Exception {
        String loginContent = "{\"username\": \"taskAccount1\", \"password\":\"password\"}";
        MvcResult result = mvc.perform(post("/authenticate/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginContent)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Cookie cookie = result.getResponse().getCookie("jwt");

        for (int j = 1; j < largeNumberOfTasks; j++) {

            // create the tasks for each account.
            String taskContent = "{\n" +
                    "    \"contactIDs\":[],\n" +
                    "    \"taskNotes\": [],\n" +
                    "    \"taskName\": \"TaskNumber" + String.valueOf(j) + "\",\n" +
                    "    \"deadline\": \"2021-11-5 04:00:00\"," +
                    "    \"priority\": 3\n" +
                    "}";

            mvc.perform(post("/task/createTask")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(taskContent)
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        // go through and delete the tasks created.
        for (int j = 1; j < largeNumberOfTasks; j++) {
            // create the tasks for each account.
            String taskContent = "{\n" +
                    "    \"taskID\": " + String.valueOf(j) + "\n" +
                    "}";

            mvc.perform(post("/task/deleteTask")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(taskContent)
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    // Will bunch create tasks, add priorities and deadlines, then delete them again.
    @Test
    public void basePerformanceTest5() throws Exception {
        String loginContent = "{\"username\": \"taskAccount1\", \"password\":\"password\"}";
        MvcResult result = mvc.perform(post("/authenticate/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginContent)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Cookie cookie = result.getResponse().getCookie("jwt");

        for (int j = 1; j < largeNumberOfTasks; j++) {

            // create the tasks for each account.
            String taskContent = "{\n" +
                    "    \"contactIDs\":[],\n" +
                    "    \"taskNotes\": [],\n" +
                    "    \"taskName\": \"TaskNumber" + String.valueOf(j) + "\"\n" +
                    "}";

            mvc.perform(post("/task/createTask")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(taskContent)
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        // add priorities to each task
        for (int j = 1; j < largeNumberOfTasks; j++) {

            // create the tasks for each account.
            String taskContent = "{\n" +
                    "    \"taskID\": " + String.valueOf(j) + ",\n" +
                    "    \"priority\": 3\n" +
                    "}";

            mvc.perform(post("/task/updatePriority")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(taskContent)
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        }



        // go through and delete the tasks created.
        for (int j = 1; j < largeNumberOfTasks; j++) {
            // create the tasks for each account.
            String taskContent = "{\n" +
                    "    \"taskID\": " + String.valueOf(j) + "\n" +
                    "}";

            mvc.perform(post("/task/deleteTask")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(taskContent)
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

}
