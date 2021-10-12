package com.noagility.personalcrm.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Minute {
    private int minuteID;
    private int meetingID;
    private int accountID;
    private String minuteText;
    private LocalDateTime minuteCreation;

    public Minute(int minuteID, int meetingID, int accountID, String minuteText, LocalDateTime minuteCreation){
        this.minuteID = minuteID;
        this.meetingID = meetingID;
        this.accountID = accountID;
        this.minuteText = minuteText;
        this.minuteCreation = minuteCreation;
    }

    public Minute(){

    }
    public int getMinuteID() {
        return this.minuteID;
    }

    public void setMinuteID(int minuteID) {
        this.minuteID = minuteID;
    }

    public int getMeetingID() {
        return this.meetingID;
    }

    public void setMeetingID(int meetingID) {
        this.meetingID = meetingID;
    }

    public int getAccountID() {
        return this.accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public String getMinuteText() {
        return this.minuteText;
    }

    public void setMinuteText(String minuteText) {
        this.minuteText = minuteText;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getMinuteCreation() {
        return this.minuteCreation;
    }

    public void setMinuteCreation(LocalDateTime minuteCreation) {
        this.minuteCreation = minuteCreation;
    }

    @Override
    public String toString() {
        return "{" +
            " minuteID='" + getMinuteID() + "'" +
            ", meetingID='" + getMeetingID() + "'" +
            ", accountID='" + getAccountID() + "'" +
            ", minuteText='" + getMinuteText() + "'" +
            ", minuteCreation='" + getMinuteCreation() + "'" +
            "}";
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

        Minute minute = (Minute) obj;

        if(
            minuteID == minute.getMinuteID()
            && meetingID == minute.getMeetingID()
            && accountID == minute.getAccountID()
            && minuteText.equals(minute.getMinuteText())
            && minuteCreation.equals(minute.getMinuteCreation())
        ){
            return true;
        }

        return false;
    }
}
