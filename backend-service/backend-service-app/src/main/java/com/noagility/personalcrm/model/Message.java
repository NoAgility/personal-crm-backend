package com.noagility.personalcrm.model;

import java.time.LocalTime;

public class Message implements Comparable<Message>{
    private int messageID;
    private int chatID;
    private int accountID;
    private LocalTime messageTime;
    private String messageText;

    public Message(int messageID, int chatID, int accountID, LocalTime messageTime, String messageText){
        this.messageID = messageID;
        this.chatID = chatID;
        this.accountID = accountID;
        this.messageTime = messageTime;
        this.messageText = messageText;
    }

    public int getMessageID(){
        return messageID;
    }

    public int getChatID(){
        return chatID;
    }

    public int getAccountID(){
        return accountID;
    }

    public LocalTime getMessageTime(){
        return messageTime;
    }

    public String getMessageText(){
        return messageText;
    }

    @Override
    public int compareTo(Message message){
        return messageID - message.getMessageID();
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

        Message message = (Message) obj;

        if(
            messageID == message.getMessageID()
            && chatID == message.getChatID()
            && accountID == message.getAccountID()
            && messageTime.equals(message.getMessageTime())
            && messageText.equals(message.getMessageText())
        ){
            return true;
        }
        return false;
    }
}
