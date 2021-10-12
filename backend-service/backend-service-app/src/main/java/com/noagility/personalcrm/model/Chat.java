package com.noagility.personalcrm.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Chat {
    private int chatID;
    private LocalDateTime chatCreation;
    private List<Message> messages = new ArrayList<>();
    private List<Account> chatParticipants = new ArrayList<>();

    public Chat(){

    }
    
    public Chat(int chatID, LocalDateTime chatCreation){
        this.chatID = chatID;
        this.chatCreation = chatCreation;
    }

    public Chat(int chatID, LocalDateTime chatCreation, List<Account> chatParticipants){
        this.chatID = chatID;
        this.chatCreation = chatCreation;
        this.chatParticipants = chatParticipants;
    }

    public Chat(int chatID, LocalDateTime chatCreation, List<Account> chatParticipants, List<Message> messages){
        this.chatID = chatID;
        this.chatCreation = chatCreation;
        this.chatParticipants = chatParticipants;
        this.messages = messages;
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

    @Override
    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }
        if(obj == null){
            return false;
        }
        if(getClass() != obj.getClass()){
            return false;
        }
        
        Chat chat = (Chat) obj;
        if(
            chatID == chat.getChatID()
            && chatCreation.equals(chat.getChatCreation())
            && messages.containsAll(chat.getMessages()) && chat.getMessages().containsAll(messages)
            && chatParticipants.containsAll(chat.getChatParticipants()) && chat.getChatParticipants().containsAll(chatParticipants)
        ){
            return true;
        }
        
        return false;
    }
}