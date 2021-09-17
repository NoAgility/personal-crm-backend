package com.noagility.personalcrm.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import com.noagility.personalcrm.PersonalCRMApplication;
import com.noagility.personalcrm.mapper.ChatRowMapper;
import com.noagility.personalcrm.model.Chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;

public class ChatService {
    @Autowired
    DataSource dataSource;
    
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    ChatRowMapper chatRowMapper;
    
    private int maxChatID;

    @EventListener(ApplicationReadyEvent.class)
    private void loadChatID(){
        String sql = "SELECT MAX(ChatID) as ChatID FROM Chats";

        try{
            maxChatID = jdbcTemplate.queryForObject(sql, Integer.class);
        }
        catch(Exception e){
            e.printStackTrace();
            maxChatID = 0;
        }
    }

    public boolean addMessage(int chatID, int accountID, String messageText){
        System.out.println(String.format("{\"chatID\":%d,\"accountID\":%d,\"messageText\":\"%s\"}", chatID, accountID, messageText));
        try{
            //  Insert new message into Messages table
            String sql = "INSERT INTO Messages(ChatID, AccountID, MessageText) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, chatID, accountID, messageText);

            return true;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean addChat(Collection<Integer> accountIDs){
        System.out.println(String.format("{\"accountIDs\":%s}", accountIDs.toString()));
        try{
            //  Insert new chat into Chats table
            String sql = "INSERT INTO Chats(ChatID) VALUES (?)";
            int chatID = ++maxChatID;
            jdbcTemplate.update(sql, chatID);

            //  Link each account to the new chat
            sql = "INSERT INTO Accounts_Chats(AccountID, ChatID) VALUES (?, ?)";
            List<Object[]> rows = new ArrayList<>();

            for(int accountID : accountIDs){
                rows.add(new Object[] {accountID, chatID});
            }

            jdbcTemplate.batchUpdate(sql, rows);

            return true;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }
}
