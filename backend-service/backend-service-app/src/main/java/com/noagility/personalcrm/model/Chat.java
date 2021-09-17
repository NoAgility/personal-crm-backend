package com.noagility.personalcrm.model;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class Chat {
    private int chatID;
    private LocalDate chatCreation;
    private Map<Integer, Message> messages = new TreeMap<>();

    public Chat(int chatID, LocalDate chatCreation){
        this.chatID = chatID;
        this.chatCreation = chatCreation;
    }

    public int getChatID(){
        return chatID;
    }

    public LocalDate getChatCreation(){
        return chatCreation;
    }

    public void addMessage(Message message){
        messages.put(message.getMessageID(), message);
    }

    public Message getMessageByID(int messageID){
        return messages.get(messageID);
    }

    public void removeMessageByID(int messageID){
        messages.remove(messageID);
    }

    public Collection<Message> getMessages(){
        return messages.values();
    }
}