package com.noagility.personalcrm.service;

import com.noagility.personalcrm.Util.JwtTokenUtil;
import com.noagility.personalcrm.mapper.AccountRowMapper;
import com.noagility.personalcrm.mapper.ChatRowMapper;
import com.noagility.personalcrm.mapper.IntegerRowMapper;
import com.noagility.personalcrm.mapper.MessageRowMapper;
import com.noagility.personalcrm.model.Account;
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

    @Autowired
    AccountRowMapper accountRowMapper;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    IntegerRowMapper integerRowMapper;

    private int maxChatID;

    @EventListener(ApplicationReadyEvent.class)
    private void loadChatID(){
        String sql = "SELECT MAX(ChatID) as ChatID FROM Chats";

        try{
            maxChatID = jdbcTemplate.queryForObject(sql, Integer.class);
        }
        catch(Exception e){
            maxChatID = 0;
        }
    }


    /**
     * <p>Adds a message to a given chat
     * @param chatID
     * @param accountID
     * @param messageText
     * @return boolean
     */
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


    /**
     * <p>Creates a chat with the given accountIDs
     * @param accountIDs
     * @return boolean
     */
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


    /**
     * <p>Deletes a message with the given messageID and chatID
     * @param messageID
     * @param chatID
     * @return boolean
     */
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


    /**
     * <p>Edits a message with the given messageID and chatID
     * @param messageID
     * @param chatID
     * @param newText
     * @return boolean
     */
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


    /**
     * <p>Returns the specified chat object for the given chatID
     * @param chatID
     * @return Chat
     */
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
            e.printStackTrace();
        }

        return null;
    }

    /**
     * <p>Returns the specified Message object by the messageID and chatID
     * @param messageID
     * @param chatID
     * @return Message
     */
    public Message getMessageByID(int messageID, int chatID){
        System.out.println(String.format("getMessageByID: {\"messageID\": %d, \"chatID\": %d}", messageID, chatID));
        try{
            //  Get message by messageID and chatID
            String sql = "SELECT * FROM Messages WHERE MessageID = ? AND ChatID = ?";
            Message message = jdbcTemplate.queryForObject(sql, messageRowMapper, messageID, chatID);

            return message;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }


    /**
     * <p>Gets all of the chats a user is in
     * @param accountID
     * @return List<Chat>
     */
    public List<Chat> getAccountChatsByID(int accountID){
        System.out.println(String.format("getAccountChatsByID: {\"accountID\": %d}", accountID));
        try{
            //  Get all ChatIDs for account
            String sql = "SELECT ChatID AS 'Integer' FROM Accounts_Chats WHERE AccountID = ?";
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
            e.printStackTrace();
        }

        return null;
    }


    /**
     * <p>Validates that the person sending the request has created the messsage
     * @param token
     * @param messageID
     * @param chatID
     * @return boolean
     */
    public boolean validateMessageCreator(String token, int messageID, int chatID){

        System.out.println(String.format("validateMessageCreator: {\"token\": \"%s\", \"messageID\": %d, \"chatID\": %d}", token, messageID, chatID));
        try{
            //  Get message by messageID and chatID
            Message message = getMessageByID(messageID, chatID);
            return jwtTokenUtil.validateTokenSender(token, message.getAccountID());
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }

    /**
     * <p>Validates that the person sending the request is a participant of the chat
     * @param token
     * @param chatID
     * @return boolean
     */
    public boolean validateChatParticipant(String token, int chatID){
        System.out.println(String.format("validateChatParticipant: {\"token\": \"%s\", \"chatID\": %d}", token, chatID));
        try{
            //  Get chat by chatID
            Chat chat = getChatByID(chatID);
            Account account = jwtTokenUtil.getAccountFromToken(token);

            return chat.containsAccountID(account.getAccountID());
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

}