package com.noagility.personalcrm.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.noagility.personalcrm.model.Chat;
import com.noagility.personalcrm.service.ChatService;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    @RequestMapping(
        value = "/createChat",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> createChat(@RequestBody Map<String, Object> payload){
        
        try{
            if(chatService.addChat((List<Integer>)payload.get("accountIDs"))){
                return ResponseEntity.ok().body("Success");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().body("Failure");
    }

    @RequestMapping(
        value = "/sendMessage",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> sendMessage(@RequestBody Map<String, Object> payload){
        try{
            if(chatService.addMessage(
                (Integer)payload.get("chatID"),
                (Integer)payload.get("accountID"),
                (String)payload.get("messageText")
            )){
                return ResponseEntity.ok().body("Success");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().body("Failure");
    }

    @RequestMapping(
        value = "deleteMessage",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> deleteMessage(@RequestBody Map<String, Object> payload){
        try{
            if(chatService.deleteMessage(
                (Integer)payload.get("messageID"),
                (Integer)payload.get("chatID")
            )){
                return ResponseEntity.ok().body("Success");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        return ResponseEntity.badRequest().body("Failure");
    }

    @RequestMapping(
        value = "editMessage",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> editMessage(@RequestBody Map<String, Object> payload){
        try{
            if(chatService.editMessage(
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

    @RequestMapping(
        value = "getChatByID",
        method = RequestMethod.GET,
        params = {
            "chatID"
        }
    )
    public ResponseEntity<Chat> getChatByID(@RequestParam int chatID){
        try{
            return ResponseEntity.ok().body(chatService.getChatByID(chatID));
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return ResponseEntity.ok().body(null);
    }
}
