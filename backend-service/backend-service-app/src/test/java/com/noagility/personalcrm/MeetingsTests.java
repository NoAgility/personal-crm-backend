package com.noagility.personalcrm;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.test.context.junit4.SpringRunner;
import javax.servlet.http.Cookie;

import com.noagility.personalcrm.deserializer.AccountDeserializer;
import com.noagility.personalcrm.deserializer.MeetingDeserializer;
import com.noagility.personalcrm.model.Account;
import com.noagility.personalcrm.model.Meeting;
import com.noagility.personalcrm.model.Minute;
import com.noagility.personalcrm.service.AccountService;
import com.noagility.personalcrm.service.MeetingService;

@RunWith(SpringRunner.class)
@ContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "/application-test.properties")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("local")
public class MeetingsTests {
    @Autowired
    MockMvc mvc;

    @Autowired
    AccountDeserializer accountDeserializer;

    @Autowired
    MeetingService meetingService;

    @Autowired
    MeetingDeserializer meetingDeserializer;

    @Autowired
    AccountService accountService;

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

    public Account getAccount(String username) throws Exception{
        return accountDeserializer.deserializeAccount(
                mvc.perform(get("/account/get?username=" + username)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString()
        );
    }

    public Meeting[] getMeetings(Cookie cookie) throws Exception{
        return meetingDeserializer.deserializeMeetings(
                mvc.perform(get("/meeting/getAccountMeetings")
                        .cookie(cookie)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse().getContentAsString()
        );
    }

    public String sendPost(String path, String content) throws Exception{
        return mvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    public String sendPost(String path, String content, Cookie cookie) throws Exception{
        return mvc.perform(post(path)
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }
    public String sendGet(String path) throws Exception{
        return mvc.perform(get(path)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    public String sendGet(String path, Cookie cookie) throws Exception{
        return mvc.perform(get(path)
                .cookie(cookie)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    public void test1_createAccounts() throws Exception{
        //  Create account 1
        String jsonCreate = new StringBuilder()
                .append("{")
                .append("'username': 'testMeetingAccount1'")
                .append(", 'password': 'testingpassword'")
                .append(", 'name': 'testingname'")
                .append(", 'dob': '2000-01-02'")
                .append("}")
                .toString().replaceAll("'", "\"");

        sendPost("/account/create", jsonCreate);

        //  Create account 2
        String jsonCreate2 = new StringBuilder()
                .append("{")
                .append("'username': 'testMeetingAccount2'")
                .append(", 'password': 'testingpassword'")
                .append(", 'name': 'testingname'")
                .append(", 'dob': '2000-01-02'")
                .append("}")
                .toString().replaceAll("'", "\"");

        sendPost("/account/create", jsonCreate2);

        //  Crate account 3
        String jsonCreate3 = new StringBuilder()
                .append("{")
                .append("'username': 'testMeetingAccount3'")
                .append(", 'password': 'testingpassword'")
                .append(", 'name': 'testingname'")
                .append(", 'dob': '2000-01-02'")
                .append("}")
                .toString().replaceAll("'", "\"");

        sendPost("/account/create", jsonCreate3);

    }

    @Test
    public void test2_createMeetings() throws Exception{
        //  User 1 creates 3 meetings with different people, user 3 should not have access to every meeting

        Account account1 = accountService.getByUsername("testMeetingAccount1");
        Account account2 = accountService.getByUsername("testMeetingAccount2");
        Account account3 = accountService.getByUsername("testMeetingAccount3");

        String jsonMeetings[] = new String[]{
                String.format("{\"accountIDs\": [%d,%d], \"meetingName\": \"Meeting 1\", \"meetingDescription\": \"Description 1\", \"meetingStart\": \"2021-11-10 04:00:00\", \"meetingEnd\": \"2021-12-10 04:00:00\"}", account1.getAccountID(), account2.getAccountID()),
                String.format("{\"accountIDs\": [%d,%d,%d], \"meetingName\": \"Meeting 2\", \"meetingDescription\": \"Description 2\", \"meetingStart\": \"2021-11-10 04:00:00\", \"meetingEnd\": \"2021-12-10 04:00:00\"}", account1.getAccountID(), account2.getAccountID(), account3.getAccountID()),
                String.format("{\"accountIDs\": [%d,%d], \"meetingName\": \"Meeting 3\", \"meetingDescription\": \"Description 3\", \"meetingStart\": \"2021-11-10 04:00:00\", \"meetingEnd\": \"2021-12-10 04:00:00\"}", account1.getAccountID(), account3.getAccountID()),
        };

        //  Create hashmaps of expected participants
        List<Map<Integer, Boolean>> expectedParticipants = Arrays.asList(
                Map.of(
                        account1.getAccountID(), true,
                        account2.getAccountID(), false
                ),
                Map.of(
                        account1.getAccountID(), true,
                        account2.getAccountID(), false,
                        account3.getAccountID(), false
                ),
                Map.of(
                        account1.getAccountID(),true,
                        account3.getAccountID(), false
                )
        );

        //  Login to users and send requets
        Cookie cookie = getCookie("testMeetingAccount1", "testingpassword");
        List<Meeting> expectedMeetings = new ArrayList<>();
        int i = 1;

        //  Send requests to make meetings
        for(String meeting : jsonMeetings){
            sendPost("/meeting/createMeeting", meeting, cookie);

            Meeting expectedMeeting = new Meeting(
                    i,
                    "Meeting " + i,
                    "Description " + i,
                    account1.getAccountID(),
                    LocalDateTime.parse("2021-11-10 04:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    LocalDateTime.parse("2021-12-10 04:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    null
            );

            expectedMeeting.setMeetingMinutes(new ArrayList<>());
            expectedMeeting.setMeetingParticipants(expectedParticipants.get(i-1));
            expectedMeetings.add(expectedMeeting);
            i++;
        }

        List<Meeting> returnedMeetings = Arrays.asList(meetingDeserializer.deserializeMeetings(
                sendGet("/meeting/getAccountMeetings", cookie)
        ));

        //  Verify that meetings are as expected
        for(i=0;i<jsonMeetings.length;i++){
            Meeting expectedMeeting = expectedMeetings.get(i);
            Meeting returnedMeeting = returnedMeetings.get(i);
            expectedMeeting.setMeetingCreation(returnedMeeting.getMeetingCreation());
            System.out.println(expectedMeeting.toString());
            System.out.println(returnedMeeting.toString());
            assertEquals(expectedMeeting, returnedMeeting);
        }

        //  Login to user 3 to verify that user 3 does not have meetings they weren't invited to
        Cookie cookie3 = getCookie("testMeetingAccount3", "testingpassword");
        expectedMeetings.remove(0);

        returnedMeetings = Arrays.asList(meetingDeserializer.deserializeMeetings(
                sendGet("/meeting/getAccountMeetings", cookie3)
        ));

        assert(expectedMeetings.equals(returnedMeetings));
    }

    @Test
    public void test3_editMeeting() throws Exception{
        //  User 1 edits meeting 3
        int meetingID = 3;

        //  Login to user 1
        Cookie cookie = getCookie("testMeetingAccount1", "testingpassword");

        //  Fetch the meeting before any changes were made
        Meeting before = meetingDeserializer.deserializeMeeting(sendGet("/meeting/getMeetingByID?meetingID=" + meetingID, cookie));

        //  Make changes to the meeting
        String newName = "New meeting name";
        String newDesc = "New meeting description";
        LocalDateTime newStart = LocalDateTime.parse("2020-11-10 04:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime newEnd = LocalDateTime.parse("2022-11-10 04:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        before.setMeetingName(newName);
        before.setMeetingDescription(newDesc);
        before.setMeetingStart(newStart);
        before.setMeetingEnd(newEnd);

        //  Send the request to edit the meeting
        String content = "{" +
                "\"meetingID\": " + meetingID + ", " +
                "\"meetingName\": \"" + newName + "\", " +
                "\"meetingDescription\": \"" + newDesc + "\", " +
                "\"meetingStart\": \"" + newStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\", " +
                "\"meetingEnd\": \"" + newEnd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\"}"
                ;

        sendPost("/meeting/editMeeting", content, cookie);

        Meeting after = meetingDeserializer.deserializeMeeting(sendGet("/meeting/getMeetingByID?meetingID=" + meetingID, cookie));

        assert(before.equals(after));
    }

    @Test
    public void test4_deleteMeeting() throws Exception{
        //  User 1 deletes meeting 2
        int meetingID = 2;

        //  Login to user 1
        Cookie cookie = getCookie("testMeetingAccount1", "testingpassword");

        //  Fetch user 1's meetings before one is deleted
        List<Meeting> before = new ArrayList<>(Arrays.asList(meetingDeserializer.deserializeMeetings(sendGet("/meeting/getAccountMeetings", cookie))));
        before.remove(1);

        //  Send the request to delete the meeting
        String content = "{\"meetingID\": " + meetingID + "}";

        sendPost("/meeting/deleteMeeting", content, cookie);

        //  Verify that the user does not see the meeting they deleted
        List<Meeting> after = Arrays.asList(meetingDeserializer.deserializeMeetings(sendGet("/meeting/getAccountMeetings", cookie)));

        assert(before.equals(after));
    }

    @Test
    public void test5_acceptMeeting() throws Exception{
        //  User 2 accepts meeting 1
        int meetingID = 1;

        //  Login to user 2
        Cookie cookie = getCookie("testMeetingAccount2", "testingpassword");
        Account account2 = accountService.getByUsername("testMeetingAccount2");
        int id = account2.getAccountID();
        //  Fetch the meeting before user 2 accepts the meeting
        Meeting before = meetingDeserializer.deserializeMeeting(sendGet("/meeting/getMeetingByID?meetingID=" + meetingID, cookie));
        assert(before.getMeetingParticipants().containsKey(id) && before.getMeetingParticipants().get(id) == false);
        before.getMeetingParticipants().put(id, true);

        //  Send the request to accept the meeting
        String content = "{\"meetingID\": " + meetingID + "}";
        sendPost("/meeting/acceptMeeting", content, cookie);

        //  Verify that the user has accepted the meeting
        Meeting after = meetingDeserializer.deserializeMeeting(sendGet("/meeting/getMeetingByID?meetingID=" + meetingID, cookie));

        assert(before.equals(after));
    }

    @Test
    public void test6_declineMeeting() throws Exception{
        //  User 3 declines meeting 3
        int meetingID = 3;

        //  Login to user 1 and 3
        Cookie cookie = getCookie("testMeetingAccount1", "testingpassword");
        Cookie cookie3 = getCookie("testMeetingAccount3", "testingpassword");
        Account account3 = accountService.getByUsername("testMeetingAccount3");
        int id = account3.getAccountID();
        //  Fetch the meeting before user 3 declines the meeting
        Meeting before = meetingDeserializer.deserializeMeeting(sendGet("/meeting/getMeetingByID?meetingID=" + meetingID, cookie3));
        assert(before.getMeetingParticipants().containsKey(id) && before.getMeetingParticipants().get(id) == false);
        before.getMeetingParticipants().remove(id);

        //  Send the request to decline the meeting
        String content = "{\"meetingID\": " + meetingID + "}";
        sendPost("/meeting/declineMeeting", content, cookie3);

        //  Verify that user 3 does not show as a participant
        Meeting after = meetingDeserializer.deserializeMeeting(sendGet("/meeting/getMeetingByID?meetingID=" + meetingID, cookie));

        assert(before.equals(after));
    }

    @Test
    public void test7_createMinute() throws Exception{
        //  User 1 adds a minute to meeting 1, and user 2 adds one to meeting 1
        int meetingID = 1;

        //  Login to user 1 and 2
        Cookie cookie = getCookie("testMeetingAccount1", "testingpassword");
        Cookie cookie2 = getCookie("testMeetingAccount2", "testingpassword");
        Account account = getAccount("testMeetingAccount1");
        Account account2 = getAccount("testMeetingAccount2");

        //  Create details of new minutes
        String minuteText = "New minute";
        String minuteText2 = "Second minute";

        String content = "{" +
                "\"meetingID\": " + meetingID + ", " +
                "\"minuteText\": \"" + minuteText + "\"}"
                ;

        String content2 = "{" +
                "\"meetingID\": " + meetingID + ", " +
                "\"minuteText\": \"" + minuteText2 + "\"}"
                ;

        //  Send requests of new minutes
        sendPost("/meeting/createMinute", content, cookie);
        sendPost("/meeting/createMinute", content2, cookie2);

        //  Verify that minutes are in the correct format
        Meeting after = meetingDeserializer.deserializeMeeting(sendGet("/meeting/getMeetingByID?meetingID=" + meetingID, cookie));
        List<Minute> returnedMinutes = after.getMeetingMinutes();

        List<Minute> expectedMinutes = Arrays.asList(
                new Minute(
                        returnedMinutes.get(0).getMinuteID(),
                        meetingID,
                        account.getAccountID(),
                        minuteText,
                        returnedMinutes.get(0).getMinuteCreation()
                ),
                new Minute(
                        returnedMinutes.get(1).getMinuteID(),
                        meetingID,
                        account2.getAccountID(),
                        minuteText2,
                        returnedMinutes.get(1).getMinuteCreation()
                )
        );

        assert(expectedMinutes.equals(returnedMinutes));
    }

    @Test
    public void test8_editMinute() throws Exception{
        //  User 1 edits the first minute in meeting1
        int meetingID = 1;
        int minuteID = 1;

        //  Login to user 1
        Cookie cookie = getCookie("testMeetingAccount1", "testingpassword");

        //  Fetch the meeting before a minute is edited
        Meeting before = meetingDeserializer.deserializeMeeting(sendGet("/meeting/getMeetingByID?meetingID=" + meetingID, cookie));

        //  Details of the edited minute
        String newMinuteText = "Edited text";
        before.getMeetingMinutes().get(0).setMinuteText(newMinuteText);

        String content = "{" +
                "\"meetingID\": " + meetingID + ", " +
                "\"minuteID\": " + minuteID + ", " +
                "\"minuteText\": \"" + newMinuteText + "\"}"
                ;

        //  Send the request to edit the minute
        sendPost("/meeting/editMinute", content, cookie);

        //  Verify that the minute was edited
        Meeting after = meetingDeserializer.deserializeMeeting(sendGet("/meeting/getMeetingByID?meetingID=" + meetingID, cookie));

        assert(before.equals(after));
    }

    @Test
    public void test9_deleteMinute() throws Exception{
        //  User 2 deletes the second minute in meeting 1
        int meetingID = 1;
        int minuteID = 2;

        //  Login to user 2
        Cookie cookie = getCookie("testMeetingAccount2", "testingpassword");

        //  Fetch the meeting before a minute is deleted
        Meeting before = meetingDeserializer.deserializeMeeting(sendGet("/meeting/getMeetingByID?meetingID=" + meetingID, cookie));
        before.getMeetingMinutes().remove(1);

        //  Send the request to delete the minute
        String content = "{" +
                "\"meetingID\": " + meetingID + ", " +
                "\"minuteID\" : " + minuteID + "}"
                ;

        sendPost("/meeting/deleteMinute", content, cookie);

        //  Verify that the minute was removed from the meeting
        Meeting after = meetingDeserializer.deserializeMeeting(sendGet("/meeting/getMeetingByID?meetingID=" + meetingID, cookie));

        assert(before.equals(after));
    }
}
