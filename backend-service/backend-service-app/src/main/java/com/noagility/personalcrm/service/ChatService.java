package com.noagility.personalcrm.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import com.noagility.personalcrm.Util.JwtTokenUtil;
import com.noagility.personalcrm.mapper.AccountRowMapper;
import com.noagility.personalcrm.mapper.ChatRowMapper;
import com.noagility.personalcrm.mapper.IntegerRowMapper;
import com.noagility.personalcrm.mapper.MessageRowMapper;
import com.noagility.personalcrm.model.Account;
import com.noagility.personalcrm.model.Chat;
import com.noagility.personalcrm.model.Message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
public class ChatService {
    @Autowired
    DataSource dataSource;
    
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    ChatRowMapper chatRowMapper;
    
    @Autowired
    MessageRowMapper messageRowMapper;

    @Autowired
    AccountRowMapper accountRowMapper;
    
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    IntegerRowMapper integerRowMapper;

    private int maxChatID;

    /**
     * Method on application start, reads from the database the current max ID and increments it to insert future
     * entries
     */
    @EventListener(ApplicationReadyEvent.class)
    private void loadChatID(){
        String sql = "SELECT MAX(ChatID) as ChatID FROM Chats";

        try{
            maxChatID = jdbcTemplate.queryForObject(sql, Integer.class);
        }
        catch(Exception e){
            maxChatID = 0;
        }
        log.info("ChatService has started, incrementing ChatID from {}", maxChatID);
    }

    
    /** 
     * <p>Adds a message to a given chat
     * @param chatID The chat to add message to
     * @param accountID The account that created the message
     * @param messageText The text of the message
     * @return boolean
     */
    public boolean addMessage(int chatID, int accountID, String messageText){
        try{
            //  Insert new message into Messages table
            String sql = "INSERT INTO Messages(ChatID, AccountID, MessageText) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, chatID, accountID, messageText);
            log.info("Message added for chat (id: {}) from account (id: {}), message: {}", chatID, accountID, messageText);
            return true;
        }
        catch(Exception e){
            log.error("Failed to create message for chat (id: {}) from account (id: {})", chatID, accountID, e);
        }
        return false;
    }

    
    /** 
     * <p>Creates a chat with the given accountIDs
     * @param accountIDs The ids of the accounts involved
     * @return a boolean indicating the success of the transaction
     */
    public boolean addChat(Collection<Integer> accountIDs){
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
            log.info("Chat {} created for accounts IDs: {}", chatID, Arrays.toString(accountIDs.toArray()));
            jdbcTemplate.batchUpdate(sql, rows);

            return true;
        }
        catch(Exception e){
            log.error("Failed to create chat for account IDs: {}", Arrays.toString(accountIDs.toArray()), e);
        }

        return false;
    }


    /**
     * <p>Deletes a message with the given messageID and chatID
     * @param messageID The id of the message to be deleted
     * @param chatID The id of the chat that contains the message
     * @return a boolean indicating the success of the transaction
     */
    public boolean deleteMessage(int messageID, int chatID){
        try{
            //  Delete message by messageID and chatID
            String sql = "DELETE FROM Messages WHERE MessageID = ? AND ChatID = ?";
            jdbcTemplate.update(sql, messageID, chatID);
            log.info("Message {} deleted for chat {}", messageID, chatID);
            return true;
        }
        catch(Exception e){
            log.error("Failed to delete message (id: {})", messageID, e);
        }

        return false;
    }

    
    /** 
     * <p>Edits a message with the given messageID and chatID
     * @param messageID The id of the message to be edited
     * @param chatID The id of the chat that contains the message
     * @param newText The new text of the message
     * @return a boolean indicating the success of the transaction
     */
    public boolean editMessage(int messageID, int chatID, String newText){
        try{
            //  Edit message by messageID and chatID
            String sql = "UPDATE Messages SET MessageText = ? WHERE MessageID = ? AND ChatID = ?";
            jdbcTemplate.update(sql, newText, messageID, chatID);
            log.info("Message {} edited for chat {}, new text: \"{}\"", messageID, chatID, newText);
            return true;
        }
        catch(Exception e){
            log.error("Failed to edit message (id: {})", messageID, e);
        }

        return false;
    }

    
    /** 
     * <p>Returns the specified chat object for the given chatID
     * @param chatID The id of the chat to look for
     * @return A Chat object
     */
    public Chat getChatByID(int chatID){
        try{
            //  Get chat by chatID
            String sql = "SELECT * FROM Chats WHERE ChatID = ?";
            Chat chat = jdbcTemplate.queryForObject(sql, chatRowMapper, chatID);
            
            //  Get messages by chatID
            sql = "SELECT * FROM Messages WHERE ChatID = ? ORDER BY MessageTime ASC";
            List<Message> messages = jdbcTemplate.query(sql, messageRowMapper, chatID);
            chat.setChatMessages(messages);

            //  Get chat participants by chatID
            sql = "SELECT Accounts.AccountID, AccountUsername, AccountName, AccountDOB, AccountCreation, AccountActive"
                + " FROM Accounts_Chats"
                + " LEFT JOIN Accounts"
                + " ON Accounts_Chats.AccountID = Accounts.AccountID"
                + " WHERE ChatID = ?";
            List<Account> accounts = jdbcTemplate.query(sql, accountRowMapper, chatID);
            chat.setChatParticipants(accounts);

            return chat;
        }
        catch(Exception e){
            log.error("Failed to fetch chat by id {}", chatID, e);
        }

        return null;
    }

    /** 
     * <p>Returns the specified Message object by the messageID and chatID
     * @param messageID The id of the message to look for
     * @param chatID The id of the chat to look for the message in
     * @return Message
     */
    public Message getMessageByID(int messageID, int chatID){
        try{
            //  Get message by messageID and chatID
            String sql = "SELECT * FROM Messages WHERE MessageID = ? AND ChatID = ?";
            Message message = jdbcTemplate.queryForObject(sql, messageRowMapper, messageID, chatID);

            return message;
        }
        catch(Exception e){
            log.error("Failed to fetch message by id {} from chat (id: {})", messageID, chatID, e);
        }

        return null;
    }

    
    /** 
     * <p>Gets all of the chats a user is in
     * @param accountID The account to fetch the chats for
     * @return List<Chat>
     */
    public List<Chat> getAccountChatsByID(int accountID){
        try{
            //  Get all ChatIDs for account
            String sql = "SELECT ChatID AS `Integer` FROM Accounts_Chats WHERE AccountID = ? AND Account_ChatActive = 1";
            List<Integer> chatIDs = jdbcTemplate.query(sql, integerRowMapper, accountID);
            
            if(chatIDs == null){
                return null;
            }

            List<Chat> chats = new ArrayList<>();
            for(int chatID : chatIDs){
                Chat chat = getChatByID(chatID);
                
                if(chat != null){
                    chats.add(chat);
                }
            }

            return chats;
        }
        catch(Exception e){
            log.error("Failed to fetch chats of account (id: {})", accountID, e);
        }

        return null;
    }

    
    /** 
     * <p>Validates that the person sending the request has created the messsage
     * @param token The JWT token from the request
     * @param messageID The id of the message
     * @param chatID The id of the chat containing the message
     * @return boolean
     */
    public boolean validateMessageCreator(String token, int messageID, int chatID){
        try{
            //  Get message by messageID and chatID
            Message message = getMessageByID(messageID, chatID);
            return jwtTokenUtil.validateTokenSender(token, message.getAccountID());
        }
        catch(Exception e){
            log.error("Failed to validate message creator for chat (id: {})", chatID, e);
        }
        
        return false;
    }

    /** 
     * <p>Validates that the person sending the request is a participant of the chat
     * @param token The JWT token from the request
     * @param chatID The id of the chat containing the message
     * @return boolean
     */
    public boolean validateChatParticipant(String token, int chatID){
        try{
            //  Get chat by chatID
            Chat chat = getChatByID(chatID);
            Account account = jwtTokenUtil.getAccountFromToken(token);

            return chat.containsAccountID(account.getAccountID());
        }
        catch(Exception e){
            log.error("Failed to validate chat participant for chat (id: {})", chatID, e);
        }
        return false;
    }

    /**
     * Method to leave an chat for an account
     * @param chatID The id of the chat to leave
     * @param accountID The id of the account to leave
     * @return boolean indicating the success of the transaction
     */
    public boolean leaveChat(int chatID, int accountID){
        try{
            String sql = "UPDATE Accounts_Chats SET Account_ChatActive = 0 WHERE ChatID = ? AND AccountID = ?";
            jdbcTemplate.update(sql, chatID, accountID);
            log.info("Account (id: {}) has left chat (id: {})", accountID, chatID);
            return true;
        }
        catch(Exception e){
            log.error("Failed to leave chat (id: {}) - account (id: {})", chatID, accountID, e);
        }
        return false;
    }

}
