package com.noagility.personalcrm.service;

import com.noagility.personalcrm.mapper.ChatRowMapper;
import com.noagility.personalcrm.mapper.MessageRowMapper;
import com.noagility.personalcrm.model.Chat;
import com.noagility.personalcrm.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChatService {
    @Autowired
    DataSource dataSource;
    
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    ChatRowMapper chatRowMapper;
    
    @Autowired
    MessageRowMapper messageRowMapper;

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
        System.out.println(String.format("addMessage: {\"chatID\":%d,\"accountID\":%d,\"messageText\":\"%s\"}", chatID, accountID, messageText));
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
        System.out.println(String.format("addChat: {\"accountIDs\":%s}", accountIDs.toString()));
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

    public boolean deleteMessage(int messageID, int chatID){
        System.out.println(String.format("removeMessage: {\"messageID\": %d, \"chatID\": %d}", messageID, chatID));
        try{
            //  Delete message by messageID and chatID
            String sql = "DELETE FROM Messages WHERE MessageID = ? AND ChatID = ?";
            jdbcTemplate.update(sql, messageID, chatID);

            return true;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public boolean editMessage(int messageID, int chatID, String newText){
        System.out.println(String.format("editMessage: {\"messageID\": %d, \"chatID\": %d, \"newText\": \"%s\"}", messageID, chatID, newText));
        try{
            //  Edit message by messageID and chatID
            String sql = "UPDATE Messages SET MessageText = ? WHERE MessageID = ? AND ChatID = ?";
            jdbcTemplate.update(sql, newText, messageID, chatID);
            
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public Chat getChatByID(int chatID){
        System.out.println(String.format("getChatByID: {\"chatID\": %d}", chatID));
        try{
            //  Get chat by chatID
            String sql = "SELECT * FROM Chats WHERE ChatID = ?";
            Chat chat = jdbcTemplate.queryForObject(sql, chatRowMapper, chatID);
            
            //  Get messages by chatID
            sql = "SELECT * FROM Messages WHERE ChatID = ? ORDER BY MessageTime ASC";
            List<Message> messages = jdbcTemplate.query(sql, messageRowMapper, chatID);
            chat.setChatMessages(messages);

            return chat;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
