package com.noagility.personalcrm;

import com.noagility.personalcrm.service.AccountService;
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
import java.util.Arrays;

import org.springframework.test.context.junit4.SpringRunner;
import javax.servlet.http.Cookie;

import com.noagility.personalcrm.deserializer.AccountDeserializer;
import com.noagility.personalcrm.deserializer.ChatDeserializer;
import com.noagility.personalcrm.model.Account;
import com.noagility.personalcrm.model.Chat;
import com.noagility.personalcrm.model.Message;

@RunWith(SpringRunner.class)
@ContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "/application-test.properties")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MessagesTests {
    @Autowired
	MockMvc mvc;
    @Autowired
    AccountDeserializer accountDeserializer;
    @Autowired
    ChatDeserializer chatDeserializer;
    @Autowired
    AccountService accountService;
    @Test
    public void test1_createChatTest() throws Exception{
        String jsonCreate = new StringBuilder()
				.append("{")
				.append("'username': 'testChatCreation'")
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
            .append("'username': 'testChatCreation2'")
            .append(", 'password': 'testingpassword'")
            .append(", 'name': 'testingname'")
            .append(", 'dob': '2000-01-02'")
            .append("}")
            .toString().replaceAll("'", "\"");
    
        mvc.perform(post("/account/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonCreate2)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());

        String returnedJson = mvc.perform(get("/account/get?username=testChatCreation")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        
        String returnedJson2 = mvc.perform(get("/account/get?username=testChatCreation2")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        Account account1 = accountDeserializer.deserializeAccount(returnedJson);
        Account account2 = accountDeserializer.deserializeAccount(returnedJson2);
        int id1 = account1.getAccountID();
        int id2 = account2.getAccountID();

        Cookie cookie = mvc.perform(post("/authenticate/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\": \"testChatCreation\", \"password\":\"testingpassword\"}")
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getCookie("jwt");

        String jsonChatCreate1 = new StringBuilder()
            .append("{")
            .append(String.format("'accountIDs': [%d, %d]", id1, id2))
            .append("}")
            .toString().replaceAll("'", "\"");

        mvc.perform(post("/chat/createChat")
            .cookie(cookie)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonChatCreate1)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());

        String jsonChatCreate2 = new StringBuilder()
            .append("{")
            .append(String.format("'accountIDs': [%d, %d, %d]", id1, id2, id2))
            .append("}")
            .toString().replaceAll("'", "\"");
        
        mvc.perform(post("/chat/createChat")
            .cookie(cookie)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonChatCreate2)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());

        String jsonChatCreate3 = new StringBuilder()
            .append("{")
            .append(String.format("'accountIDs': [%d]", id2))
            .append("}")
            .toString().replaceAll("'", "\"");

        mvc.perform(post("/chat/createChat")
            .cookie(cookie)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonChatCreate3)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());

        returnedJson = mvc.perform(get("/chat/getAccountChats")
            .cookie(cookie)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        Chat chats[] = chatDeserializer.deserializeChats(returnedJson);

        System.out.println("Assert: 3 chats are made");
        assert(chats.length == 3);

        Chat testChats[] = {
            new Chat(1, chats[0].getChatCreation(), Arrays.asList(new Account[] {account1, account2})),
            new Chat(2, chats[1].getChatCreation(), Arrays.asList(new Account[] {account1, account2})),
            new Chat(3, chats[2].getChatCreation(), Arrays.asList(new Account[] {account1, account2})),
        };

        System.out.println("Assert: Chats are as expected");
        assert(Arrays.equals(testChats, chats));

        LocalDateTime time = chats[0].getChatCreation();

        System.out.println("Assert: All chats have the same participants");
        for(int i=1;i<chats.length;i++){
            chats[i].setChatCreation(time);
            chats[i].setChatID(1);
            assert(chats[0].equals(chats[i]));
        }
    }

    @Test
    public void test2_sendChatMessage() throws Exception{
        Cookie cookie1 = mvc.perform(post("/authenticate/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\": \"testChatCreation\", \"password\":\"testingpassword\"}")
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getCookie("jwt");

        Cookie cookie2 = mvc.perform(post("/authenticate/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\": \"testChatCreation2\", \"password\":\"testingpassword\"}")
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getCookie("jwt");

        String messageText1 = "Sending first message to chat 1 from user 1";
        String jsonSendMessage1 = String.format("{\"chatID\": 1, \"messageText\": \"%s\"}", messageText1);
        mvc.perform(post("/chat/sendMessage")
            .cookie(cookie1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonSendMessage1)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());

        String messageText2 = "Sending first message to chat 2 from user 1";
        String jsonSendMessage2 = String.format("{\"chatID\": 2, \"messageText\": \"%s\"}", messageText2);
        mvc.perform(post("/chat/sendMessage")
            .cookie(cookie1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonSendMessage2)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());

        String messageText3 = "Sending second message to chat 1 from user 2";
        String jsonSendMessage3 = String.format("{\"chatID\": 1, \"messageText\": \"%s\"}", messageText3);
        mvc.perform(post("/chat/sendMessage")
            .cookie(cookie2)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonSendMessage3)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());

        String returnedJson = mvc.perform(get("/chat/getAccountChats")
            .cookie(cookie1)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        Account account1 = accountService.getByUsername("testChatCreation");
        Account account2 = accountService.getByUsername("testChatCreation2");
        Chat chats[] = chatDeserializer.deserializeChats(returnedJson);
        Chat testChats[] = {
            new Chat(1, chats[0].getChatCreation(), chats[0].getChatParticipants(), Arrays.asList(new Message[] {
                new Message(1, 1, account1.getAccountID(), chats[0].getMessages().get(0).getMessageTime(), messageText1),
                new Message(3, 1, account2.getAccountID(), chats[0].getMessages().get(1).getMessageTime(), messageText3)
            })),
            new Chat(2, chats[1].getChatCreation(), chats[1].getChatParticipants(), Arrays.asList(new Message[] {
                new Message(2, 2, account1.getAccountID(), chats[1].getMessages().get(0).getMessageTime(), messageText2)
            })),
            new Chat(3, chats[2].getChatCreation(), chats[2].getChatParticipants(), Arrays.asList(new Message[] {}))
        };

        System.out.println("Assert: All chats are as expected");
        assert(Arrays.equals(chats, testChats));

        returnedJson = mvc.perform(get("/chat/getChatByID?chatID=1")
            .cookie(cookie1)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        Chat chat = chatDeserializer.deserializeChat(returnedJson);

        System.out.println("Assert: Specific chat is as expected");
        assert(chat.equals(chats[0]));
    }

    @Test
    public void test3_editMessage() throws Exception{
        Cookie cookie = mvc.perform(post("/authenticate/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\": \"testChatCreation\", \"password\":\"testingpassword\"}")
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getCookie("jwt");

        String returnedJson = mvc.perform(get("/chat/getAccountChats")
            .cookie(cookie)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        Chat before[] = chatDeserializer.deserializeChats(returnedJson);
        String editMessage = "This the edit";
        String jsonEditMessage = String.format("{\"chatID\": 1, \"messageID\": 1, \"newText\": \"%s\"}", editMessage);

        mvc.perform(post("/chat/editMessage")
            .cookie(cookie)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonEditMessage)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());

        returnedJson = mvc.perform(get("/chat/getAccountChats")
            .cookie(cookie)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        Chat after[] = chatDeserializer.deserializeChats(returnedJson);
        before[0].getMessages().get(0).setMessageText(editMessage);

        System.out.println("Assert: Editing a message");
        assert(Arrays.equals(before, after));
    }

    @Test
    public void test4_deleteMessage() throws Exception{
        Cookie cookie = mvc.perform(post("/authenticate/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\": \"testChatCreation2\", \"password\":\"testingpassword\"}")
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getCookie("jwt");

        String returnedJson = mvc.perform(get("/chat/getAccountChats")
            .cookie(cookie)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        Chat before[] = chatDeserializer.deserializeChats(returnedJson);
        String jsonDeleteMessage = "{\"chatID\": 1, \"messageID\": 3}";

        mvc.perform(post("/chat/deleteMessage")
            .cookie(cookie)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonDeleteMessage)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
        
        returnedJson = mvc.perform(get("/chat/getAccountChats")
            .cookie(cookie)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        Chat after[] = chatDeserializer.deserializeChats(returnedJson);
        before[0].getMessages().remove(1);

        System.out.println("Assert: Removing a message");
        assert(Arrays.equals(before, after));
    }

    @Test
    public void test5_leaveChat() throws Exception{
        Cookie cookie = mvc.perform(post("/authenticate/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\": \"testChatCreation\", \"password\":\"testingpassword\"}")
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getCookie("jwt");

        String returnedJson = mvc.perform(get("/chat/getAccountChats")
            .cookie(cookie)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        Chat before[] = chatDeserializer.deserializeChats(returnedJson);
        String jsonLeaveChat = "{\"chatID\": 1}";
        
        mvc.perform(post("/chat/leaveChat")
            .cookie(cookie)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonLeaveChat)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());

        returnedJson = mvc.perform(get("/chat/getAccountChats")
            .cookie(cookie)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        Chat after[] = chatDeserializer.deserializeChats(returnedJson);

        System.out.println("Assert: Leaving a chat");
        assert(before.length - after.length == 1);
    }
}
