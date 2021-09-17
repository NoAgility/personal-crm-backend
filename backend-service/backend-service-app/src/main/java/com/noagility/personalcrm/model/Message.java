package com.noagility.personalcrm.model;

import java.time.LocalDate;

public class Message implements Comparable<Message>{
    private int messageID;
    private int chatID;
    private int accountID;
    private LocalDate messageTime;
    private String messageText;

    public Message(int messageID, int chatID, int accountID, LocalDate messageTime, String messageText){
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

    public LocalDate getMessageTime(){
        return messageTime;
    }

    public String getMessageText(){
        return messageText;
    }

    public void setMessageID(int messageID){
        this.messageID = messageID;
    }

    public void setChatID(int chatID){
        this.chatID = chatID;
    }

    public void setAccountID(int accountID){
        this.accountID = accountID;
    }

    public void setMessageTime(LocalDate messageTime){
        this.messageTime = messageTime;
    }

    public void setMessageText(String messageText){
        this.messageText = messageText;
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
