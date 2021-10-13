package com.noagility.personalcrm;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
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
import com.noagility.personalcrm.service.MeetingService;

@RunWith(SpringRunner.class)
@ContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "/application-test.properties")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MeetingsTests {
    @Autowired
	MockMvc mvc;

    @Autowired
    AccountDeserializer accountDeserializer;

    @Autowired
    MeetingService meetingService;

    @Autowired
    MeetingDeserializer meetingDeserializer;

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
        String jsonCreate = new StringBuilder()
            .append("{")
            .append("'username': 'testMeetingAccount1'")
            .append(", 'password': 'testingpassword'")
            .append(", 'name': 'testingname'")
            .append(", 'dob': '2000-01-02'")
            .append("}")
            .toString().replaceAll("'", "\"");
        
        sendPost("/account/create", jsonCreate);
        
        String jsonCreate2 = new StringBuilder()
            .append("{")
            .append("'username': 'testMeetingAccount2'")
            .append(", 'password': 'testingpassword'")
            .append(", 'name': 'testingname'")
            .append(", 'dob': '2000-01-02'")
            .append("}")
            .toString().replaceAll("'", "\"");

        sendPost("/account/create", jsonCreate2);

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
        String jsonMeetings[] = new String[]{
            "{\"accountIDs\": [1,2], \"meetingName\": \"Meeting 1\", \"meetingDescription\": \"Description 1\", \"meetingStart\": \"2021-11-10 04:00:00\", \"meetingEnd\": \"2021-12-10 04:00:00\"}",
            "{\"accountIDs\": [1,2,3], \"meetingName\": \"Meeting 2\", \"meetingDescription\": \"Description 2\", \"meetingStart\": \"2021-11-10 04:00:00\", \"meetingEnd\": \"2021-12-10 04:00:00\"}",
            "{\"accountIDs\": [1,3], \"meetingName\": \"Meeting 3\", \"meetingDescription\": \"Description 3\", \"meetingStart\": \"2021-11-10 04:00:00\", \"meetingEnd\": \"2021-12-10 04:00:00\"}",
        };
        
        List<Map<Integer, Boolean>> expectedParticipants = Arrays.asList(
            Map.of(
                1, true,
                2, false
            ),
            Map.of(
                1, true,
                2, false,
                3, false
            ),
            Map.of(
                1,true,
                3, false
            )
        );

        Cookie cookie = getCookie("testMeetingAccount1", "testingpassword");
        List<Meeting> expectedMeetings = new ArrayList<>();
        int i = 1;
        for(String meeting : jsonMeetings){
            sendPost("/meeting/createMeeting", meeting, cookie);

            Meeting expectedMeeting = new Meeting(
                i, 
                "Meeting " + i, 
                "Description " + i, 
                1, 
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

        for(i=0;i<jsonMeetings.length;i++){
            Meeting expectedMeeting = expectedMeetings.get(i);
            Meeting returnedMeeting = returnedMeetings.get(i);
            expectedMeeting.setMeetingCreation(returnedMeeting.getMeetingCreation());
            System.out.println(expectedMeeting.toString());
            System.out.println(returnedMeeting.toString());
            assert(expectedMeeting.equals(returnedMeeting));
        }

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
        Cookie cookie = getCookie("testMeetingAccount1", "testingpassword");

        Meeting before = meetingDeserializer.deserializeMeeting(sendGet("/meeting/getMeetingByID?meetingID=" + meetingID, cookie));

        String newName = "New meeting name";
        String newDesc = "New meeting description";
        LocalDateTime newStart = LocalDateTime.parse("2020-11-10 04:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime newEnd = LocalDateTime.parse("2022-11-10 04:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        before.setMeetingName(newName);
        before.setMeetingDescription(newDesc);
        before.setMeetingStart(newStart);
        before.setMeetingEnd(newEnd);

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
        Cookie cookie = getCookie("testMeetingAccount1", "testingpassword");

        List<Meeting> before = new ArrayList<>(Arrays.asList(meetingDeserializer.deserializeMeetings(sendGet("/meeting/getAccountMeetings", cookie))));
        before.remove(1);

        String content = "{\"meetingID\": " + meetingID + "}";

        sendPost("/meeting/deleteMeeting", content, cookie);

        List<Meeting> after = Arrays.asList(meetingDeserializer.deserializeMeetings(sendGet("/meeting/getAccountMeetings", cookie)));

        assert(before.equals(after));
    }

    @Test
    public void test5_acceptMeeting() throws Exception{
        //  User 2 accepts meeting 1
        int meetingID = 1;
        Cookie cookie = getCookie("testMeetingAccount2", "testingpassword");

        Meeting before = meetingDeserializer.deserializeMeeting(sendGet("/meeting/getMeetingByID?meetingID=" + meetingID, cookie));
        assert(before.getMeetingParticipants().containsKey(2) && before.getMeetingParticipants().get(2) == false);
        before.getMeetingParticipants().put(2, true);

        String content = "{\"meetingID\": " + meetingID + "}";
        sendPost("/meeting/acceptMeeting", content, cookie);
        Meeting after = meetingDeserializer.deserializeMeeting(sendGet("/meeting/getMeetingByID?meetingID=" + meetingID, cookie));

        assert(before.equals(after));
    }

    @Test
    public void test6_declineMeeting() throws Exception{
        //  User 3 declines meeting 3
        int meetingID = 3;
        Cookie cookie = getCookie("testMeetingAccount1", "testingpassword");
        Cookie cookie3 = getCookie("testMeetingAccount3", "testingpassword");

        Meeting before = meetingDeserializer.deserializeMeeting(sendGet("/meeting/getMeetingByID?meetingID=" + meetingID, cookie3));
        assert(before.getMeetingParticipants().containsKey(3) && before.getMeetingParticipants().get(3) == false); 
        before.getMeetingParticipants().remove(3);

        String content = "{\"meetingID\": " + meetingID + "}";
        sendPost("/meeting/declineMeeting", content, cookie3);
        Meeting after = meetingDeserializer.deserializeMeeting(sendGet("/meeting/getMeetingByID?meetingID=" + meetingID, cookie));

        assert(before.equals(after));
    }

    @Test
    public void test7_createMinute() throws Exception{
        //  User 1 adds a minute to meeting 1, and user 2 adds one to meeting 1
        int meetingID = 1;
        Cookie cookie = getCookie("testMeetingAccount1", "testingpassword");
        Cookie cookie2 = getCookie("testMeetingAccount2", "testingpassword");
        Account account = getAccount("testMeetingAccount1");
        Account account2 = getAccount("testMeetingAccount2");

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

        sendPost("/meeting/createMinute", content, cookie);
        sendPost("/meeting/createMinute", content2, cookie2);
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
        Cookie cookie = getCookie("testMeetingAccount1", "testingpassword");
        Meeting before = meetingDeserializer.deserializeMeeting(sendGet("/meeting/getMeetingByID?meetingID=" + meetingID, cookie));
        
        String newMinuteText = "Edited text";
        before.getMeetingMinutes().get(0).setMinuteText(newMinuteText);

        String content = "{" +
            "\"meetingID\": " + meetingID + ", " +
            "\"minuteID\": " + minuteID + ", " +
            "\"minuteText\": \"" + newMinuteText + "\"}"
        ;

        sendPost("/meeting/editMinute", content, cookie);
        Meeting after = meetingDeserializer.deserializeMeeting(sendGet("/meeting/getMeetingByID?meetingID=" + meetingID, cookie));

        assert(before.equals(after));
    }

    @Test
    public void test9_deleteMinute() throws Exception{
        //  User 2 deletes the second minute in meeting 1
        int meetingID = 1;
        int minuteID = 2;
        Cookie cookie = getCookie("testMeetingAccount2", "testingpassword");
        Meeting before = meetingDeserializer.deserializeMeeting(sendGet("/meeting/getMeetingByID?meetingID=" + meetingID, cookie));
        before.getMeetingMinutes().remove(1);

        String content = "{" + 
            "\"meetingID\": " + meetingID + ", " +
            "\"minuteID\" : " + minuteID + "}"
        ;

        sendPost("/meeting/deleteMinute", content, cookie);
        Meeting after = meetingDeserializer.deserializeMeeting(sendGet("/meeting/getMeetingByID?meetingID=" + meetingID, cookie));

        assert(before.equals(after));
    }
}
