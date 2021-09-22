package com.noagility.personalcrm.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Chat {
    private int chatID;
    private LocalDateTime chatCreation;
    private List<Message> messages = new ArrayList<>();
    private List<Account> chatParticipants = new ArrayList<>();

    public Chat(int chatID, LocalDateTime chatCreation){
        this.chatID = chatID;
        this.chatCreation = chatCreation;
    }

    public int getChatID(){
        return chatID;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getChatCreation(){
        return chatCreation;
    }

    public void setChatID(int chatID){
        this.chatID = chatID;
    }

    public void setChatCreation(LocalDateTime chatCreation){
        this.chatCreation = chatCreation;
    }

    public void setChatMessages(List<Message> messages){
        this.messages = messages;
    }

    public void setChatParticipants(List<Account> chatParticipants){
        this.chatParticipants = chatParticipants;
    }

    public List<Message> getMessages(){
        return messages;
    }

    public List<Account> getChatParticipants(){
        return chatParticipants;
    }

    public boolean containsAccountID(int accountID){
        for(Account account : chatParticipants){
            if(account.getAccountID() == accountID){
                return true;
            }
        }

        return false;
    }
}