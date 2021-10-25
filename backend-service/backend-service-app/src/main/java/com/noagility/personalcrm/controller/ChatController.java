package com.noagility.personalcrm.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.noagility.personalcrm.Util.JwtTokenUtil;
import com.noagility.personalcrm.model.Account;
import com.noagility.personalcrm.model.Chat;
import com.noagility.personalcrm.service.ChatService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/chat")
public class ChatController {
    
    @Autowired
    private ChatService chatService;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    
    /**
     * API endpoint to create a chat between accounts
     * @param payload The payload of the request containing the ids of the accounts to include
     * @param token The JWT token used to authenticate the request
     * @return ResponseEntity<String> Indicating the success of the request
     */
    @RequestMapping(
        value = "/createChat",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> createChat(@RequestBody Map<String, Object> payload, @CookieValue("jwt") String token){
        
        try{
            List<Integer> participants = (List<Integer>)payload.get("accountIDs");
            Account account = jwtTokenUtil.getAccountFromToken(token);
            participants.add(account.getAccountID());
            Set<Integer> set = new HashSet<>(participants);
            participants.clear();
            participants.addAll(set);
            
            if(
                participants.contains(account.getAccountID())
                && chatService.addChat(participants)
            ){
                return ResponseEntity.ok().body("Success");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().body("Failure");
    }

    
    /** 
     * <p>Sends a message to the given chatID
     * @param payload
     * @param token
     * @return ResponseEntity<String>
     */
    @RequestMapping(
        value = "/sendMessage",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> sendMessage(@RequestBody Map<String, Object> payload, @CookieValue("jwt") String token){
        try{
            Account account = jwtTokenUtil.getAccountFromToken(token);
            if(
                chatService.validateChatParticipant(
                    token,
                    (Integer)payload.get("chatID")
                )
                && chatService.addMessage(
                    (Integer)payload.get("chatID"),
                    account.getAccountID(),
                    (String)payload.get("messageText")
                )
            ){
                return ResponseEntity.ok().body("Success");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().body("Failure");
    }

    
    /** 
     * <p>Deletes a message with the given messageID and chatID
     * @param payload
     * @param token
     * @return ResponseEntity<String>
     */
    @RequestMapping(
        value = "/deleteMessage",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> deleteMessage(@RequestBody Map<String, Object> payload, @CookieValue("jwt") String token){
        try{
            if(
                chatService.validateMessageCreator(
                    token, 
                    (Integer)payload.get("messageID"), 
                    (Integer)payload.get("chatID")
                )
                && chatService.deleteMessage(
                    (Integer)payload.get("messageID"),
                    (Integer)payload.get("chatID")
                )
            ){
                return ResponseEntity.ok().body("Success");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        return ResponseEntity.badRequest().body("Failure");
    }

    
    /** 
     * <p>Edits a message with the given messageID and chatID
     * @param payload
     * @param token
     * @return ResponseEntity<String>
     */
    @RequestMapping(
        value = "/editMessage",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> editMessage(@RequestBody Map<String, Object> payload, @CookieValue("jwt") String token){
        try{
            if(
                chatService.validateMessageCreator(
                    token, 
                    (Integer)payload.get("messageID"), 
                    (Integer)payload.get("chatID")
                )
                && chatService.editMessage(
                (Integer)payload.get("messageID"), 
                (Integer)payload.get("chatID"), 
                (String)payload.get("newText")
            )){
                return ResponseEntity.ok().body("Success");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().body("Failure");
    }

    
    /** 
     * <p>Gets all of the messages in a chat with the given chatID
     * @return ResponseEntity<Chat>
     */
    @RequestMapping(
        value = "/getChatByID",
        method = RequestMethod.GET,
        params = {
            "chatID"
        }
    )
    public ResponseEntity<Chat> getChatByID(@RequestParam int chatID, @CookieValue("jwt") String token){
        try{
            if(
                chatService.validateChatParticipant(
                    token, 
                    chatID
                )
            ){
                return ResponseEntity.ok().body(chatService.getChatByID(chatID));
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return ResponseEntity.ok().body(null);
    }

    
    /** 
     * <p>Gets all of the chats for the user
     * @param token
     * @return ResponseEntity<List<Chat>>
     */
    @RequestMapping(
        value = "/getAccountChats",
        method = RequestMethod.GET
    )
    public ResponseEntity<List<Chat>> getAccountChatsByID(@CookieValue("jwt") String token){
        try{
            Account account = jwtTokenUtil.getAccountFromToken(token);
            return ResponseEntity.ok().body(chatService.getAccountChatsByID(account.getAccountID()));

        }
        catch(Exception e){
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().body(null);
    }

    /**
     * API endpoint for an account to leave a chat
     * @param payload The payload containing the id of the chat to leave
     * @param token The JWT token used to authenticate the request and indicate the owner of
     * @return A responseEntity indicating the success of the request
     */
    @RequestMapping(
        value = "/leaveChat",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> leaveChat(@RequestBody Map<String, Object> payload, @CookieValue("jwt") String token){
        try{
            int chatID = (Integer)payload.get("chatID");
            Account account = jwtTokenUtil.getAccountFromToken(token);
            if(
                chatService.validateChatParticipant(token, chatID)
                && chatService.leaveChat(chatID, account.getAccountID())
            ){
                return ResponseEntity.ok().body("Success");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().body("Failure");
    }
}
