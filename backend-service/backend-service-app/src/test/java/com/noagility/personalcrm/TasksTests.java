package com.noagility.personalcrm;

import com.noagility.personalcrm.Util.JwtTokenUtil;
import com.noagility.personalcrm.deserializer.AccountDeserializer;
import com.noagility.personalcrm.deserializer.ContactDeserializer;
import com.noagility.personalcrm.service.AccountService;
import org.hamcrest.Matchers;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "/application-test.properties")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TasksTests {

    @Autowired
    MockMvc mvc;
    @Autowired
    AccountDeserializer accountDeserializer;
    @Autowired
    ContactDeserializer contactDeserializer;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    // Basic API Testing first before performance testing.
    @Autowired
    AccountService accountService;


    // to run faster or to test more performance change the number of accounts created.
    private int numberOfAccounts = 21;
    private int numberOfAccountsWithTasks = 11;
    private int largeNumberOfTasks = 5;

    public Cookie getCookie(String username, String password) throws Exception{
        return mvc.perform(post("/authenticate/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"username\": \"%s\", \"password\":\"%s\"}", username, password))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getCookie("jwt");
    }
    // create numberOfAccounts test accounts which will be used in the integration testing for adding to tasks.
    // Have all users add each other as contacts for later.
    @Test
    public void test1_createUsers() throws Exception {
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

            Cookie cookie = getCookie("taskAccount" + String.valueOf(i), "password");

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
    public void test2_taskAPIPerformanceTest() throws Exception {

        Cookie cookie = getCookie("taskAccount1", "password");

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

        // go through and check that the tasks are created correctly.
        for (int j = 1; j < largeNumberOfTasks; j++) {

            // create the tasks for each account.
            String taskContent = "{\n" +
                    "    \"taskID\": " + String.valueOf(j) + "\n" +
                    "}";
            String output = createExpectedResponse(jwtTokenUtil.getAccountFromToken(cookie.getValue()).getAccountID(), "TaskNumber" + String.valueOf(j), j);
            mvc.perform(get("/task/readTask")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(taskContent)).andExpect(content().json(output));
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

        // after deletion read nothing
        mvc.perform(get("/task/readTasks")
                .cookie(cookie))
                .andExpect(content().string("[]"));
    }

    // the second performance test will create a large number of tasks for 1 user with deadline
    @Test
    public void test3_taskAPIWithDeadline() throws Exception {
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
    public void test4_taskAPIWithDeadline_2() throws Exception {

        Cookie cookie = getCookie("taskAccount1", "password");

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
    public void test5_taskAPIWithDeadlinePriority() throws Exception {

        Cookie cookie = getCookie("taskAccount1", "password");

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
    public void test6_taskAPICreateDelete() throws Exception {

        Cookie cookie = getCookie("taskAccount1", "password");

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

        // add deadline to each task
        for (int j = 1; j < largeNumberOfTasks; j++) {
            // create the tasks for each account.
            String taskContent = "{\n" +
                    "    \"taskID\": " + String.valueOf(j) + ",\n" +
                    "    \"deadline\": \"2021-11-5 04:00:00\"" +
                    "}";
            mvc.perform(post("/task/updateDeadline")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(taskContent)
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        // go through and delete priorities.
        for (int j = 1; j < largeNumberOfTasks; j++) {
            // create the tasks for each account.
            String taskContent = "{\n" +
                    "    \"taskID\": " + String.valueOf(j) + "\n" +
                    "}";
            mvc.perform(post("/task/deletePriority")
                    .cookie(cookie)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(taskContent)
                    .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        // go through and delete deadlines.
        for (int j = 1; j < largeNumberOfTasks; j++) {
            // create the tasks for each account.
            String taskContent = "{\n" +
                    "    \"taskID\": " + String.valueOf(j) + "\n" +
                    "}";
            mvc.perform(post("/task/deleteDeadline")
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

    @Test
    public void test7_taskAPITest() throws Exception {
        // log into the first user.
        Cookie cookie = getCookie("taskAccount1", "password");

        // create the task
        String taskContent = "{\n" +
                "    \"contactIDs\":[],\n" +
                "    \"taskNotes\": [],\n" +
                "    \"taskName\": \"TaskNumber" + String.valueOf(1) + "\"\n" +
                "}";

        mvc.perform(post("/task/createTask")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskContent)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // create the task 2
        taskContent = "{\n" +
                "    \"contactIDs\":[],\n" +
                "    \"taskNotes\": [],\n" +
                "    \"taskName\": \"TaskNumber" + String.valueOf(2) + "\"\n" +
                "}";

        mvc.perform(post("/task/createTask")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskContent)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());


        // read the tasks
        String output = mvc.perform(get("/task/readTasks")
                .cookie(cookie)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println(output);


        // add a contact.
        int contactId3 = accountService.getByUsername("taskAccount3").getAccountID();
        taskContent = String.format("{\n" +
                "    \"taskID\": 5,\n" +
                "    \"contactID\": %d\n" +
                "}", contactId3);

        mvc.perform(post("/task/addTaskContact")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskContent)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // add a task note.

        taskContent = "{\n" +
                "    \"taskID\": 5,\n" +
                "    \"taskNote\": \"NoteNumber1\"\n" +
                "}";

        mvc.perform(post("/task/addTaskNote")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskContent)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        taskContent = "{\n" +
                "    \"taskID\": 5\n" +
                "}";


        output = String.format("\"taskName\":\"TaskNumber1\",\"taskDeadline\":null,\"taskPriority\":3,\"taskComplete\":0,\"taskNoteList\":[{\"taskID\":5,\"taskNoteID\":1,\"note\":\"NoteNumber1\"}],\"taskContactAccounts\":[{\"taskID\":5,\"contactID\":%d}],\"owner\":false,\"taskID\":5}", contactId3);


        mvc.perform(get("/task/readTask")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskContent))
                .andExpect(content().string(Matchers.containsString(output)));

        // update the task note

        taskContent = "{\n" +
                "    \"taskID\": 5,\n" +
                "    \"taskNoteID\": 1,\n" +
                "    \"newTaskNote\": \"NEW NOTE HELLO WORLD\"\n" +
                "}";

        // update the task note.

        mvc.perform(post("/task/updateTaskNote")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskContent)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        taskContent = "{\n" +
                "    \"taskID\": 5\n" +
                "}";

        taskContent = "{\n" +
                "    \"taskID\": 5\n" +
                "}";

        output = String.format("\"taskName\":\"TaskNumber1\",\"taskDeadline\":null,\"taskPriority\":3,\"taskComplete\":0,\"taskNoteList\":[{\"taskID\":5,\"taskNoteID\":1,\"note\":\"NEW NOTE HELLO WORLD\"}],\"taskContactAccounts\":[{\"taskID\":5,\"contactID\":%d}],\"owner\":false,\"taskID\":5}", contactId3);
        mvc.perform(get("/task/readTask")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskContent))
                .andExpect(content().string(Matchers.containsString(output)));


        taskContent = "{\n" +
                "    \"taskNoteID\": 1\n" +
                "}";

        // delete the task note

        mvc.perform(post("/task/deleteTaskNote")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskContent)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());


        taskContent = "{\n" +
                "    \"taskID\": 5\n" +
                "}";


        output = String.format("\"taskName\":\"TaskNumber1\",\"taskDeadline\":null,\"taskPriority\":3,\"taskComplete\":0,\"taskNoteList\":[],\"taskContactAccounts\":[{\"taskID\":5,\"contactID\":%d}],\"owner\":false,\"taskID\":5}", contactId3);
        mvc.perform(get("/task/readTask")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskContent))
                .andExpect(content().string(Matchers.containsString(output)));

        // Delete the Task Contact.

        taskContent = String.format("{\n" +
                "    \"taskID\": 5,\n" +
                "    \"contactID\": %d\n" +
                "}", contactId3);

        // delete the task note

        mvc.perform(post("/task/deleteContact")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskContent)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());


        taskContent = "{\n" +
                "    \"taskID\": 5\n" +
                "}";


        output = "\"taskName\":\"TaskNumber1\",\"taskDeadline\":null,\"taskPriority\":3,\"taskComplete\":0,\"taskNoteList\":[],\"taskContactAccounts\":[],\"owner\":false,\"taskID\":5}";
        mvc.perform(get("/task/readTask")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskContent))
                .andExpect(content().string(Matchers.containsString(output)));


    }

    private String createExpectedResponse(int accountID, String taskName, int taskID){

        String output = "{\n" +
                "    \"accountID\": " + String.valueOf(accountID) + ",\n" +
                "    \"taskName\": \"" + taskName + "\",\n" +
                "    \"taskDeadline\": null,\n" +
                "    \"taskComplete\":0,\n" +
                "    \"taskPriority\": -1,\n" +
                "    \"taskNoteList\": [],\n" +
                "    \"owner\":false,\n" +
                "    \"taskContactAccounts\": [],\n" +
                "    \"taskID\": " + String.valueOf(taskID) + "\n" +
                "}";
        return output;
    }

    private String createExpectedResponse(int accountID, String taskName, int taskID, int priority){

        String output = "{\n" +
                "    \"accountID\": " + String.valueOf(accountID) + ",\n" +
                "    \"taskName\": \"" + taskName + "\",\n" +
                "    \"taskDeadline\": null,\n" +
                "    \"taskComplete\":0,\n" +
                "    \"taskPriority\": " + String.valueOf(priority) + ",\n" +
                "    \"taskNoteList\": [],\n" +
                "    \"owner\":false,\n" +
                "    \"taskContactAccounts\": [],\n" +
                "    \"taskID\": " + String.valueOf(taskID) + "\n" +
                "}";
        return output;
    }

    private String createExpectedResponse(int accountID, String taskName, int taskID, String deadline){

        String output = "{\n" +
                "    \"accountID\": " + String.valueOf(accountID) + ",\n" +
                "    \"taskName\": \"" + taskName + "\",\n" +
                "    \"taskDeadline\": \"" + deadline + "\",\n" +
                "    \"taskComplete\":0,\n" +
                "    \"taskPriority\": -1,\n" +
                "    \"taskNoteList\": [],\n" +
                "    \"owner\":false,\n" +
                "    \"taskContactAccounts\": [],\n" +
                "    \"taskID\": " + String.valueOf(taskID) + "\n" +
                "}";
        return output;
    }

    private String createExpectedResponse(int accountID, String taskName, int taskID, String deadline, int priority){

        String output = "{\n" +
                "    \"accountID\": " + String.valueOf(accountID) + ",\n" +
                "    \"taskName\": \"" + taskName + "\",\n" +
                "    \"taskDeadline\": \"" + deadline + "\",\n" +
                "    \"taskComplete\":0,\n" +
                "    \"taskPriority\": " + String.valueOf(priority) + ",\n" +
                "    \"taskNoteList\": [],\n" +
                "    \"owner\":false,\n" +
                "    \"taskContactAccounts\": [],\n" +
                "    \"taskID\": " + String.valueOf(taskID) + "\n" +
                "}";
        return output;
    }

}
