package com.noagility.personalcrm.model;

import java.time.LocalDateTime;
import java.util.List;

public class Meeting {
    private int meetingID;
    private String meetingName;
    private String meetingDescription;
    private int meetingCreatorID;
    private LocalDateTime meetingStart;
    private LocalDateTime meetingEnd;
    private List<Account> meetingParticipants;

    public Meeting(int meetingID, String meetingName, String meetingDescription, int meetingCreatorID, LocalDateTime meetingStart, LocalDateTime meetingEnd){
        this.meetingID = meetingID;
        this.meetingName = meetingName;
        this.meetingDescription = meetingDescription;
        this.meetingCreatorID = meetingCreatorID;
        this.meetingStart = meetingStart;
        this.meetingEnd = meetingEnd;
    }

    public Meeting(){
        
    }

    public int getMeetingID() {
        return this.meetingID;
    }

    public void setMeetingID(int meetingID) {
        this.meetingID = meetingID;
    }

    public String getMeetingName() {
        return this.meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public String getMeetingDescription() {
        return this.meetingDescription;
    }

    public void setMeetingDescription(String meetingDescription) {
        this.meetingDescription = meetingDescription;
    }

    public int getMeetingCreatorID() {
        return this.meetingCreatorID;
    }

    public void setMeetingCreatorID(int meetingCreatorID) {
        this.meetingCreatorID = meetingCreatorID;
    }

    public LocalDateTime getMeetingStart() {
        return this.meetingStart;
    }

    public void setMeetingStart(LocalDateTime meetingStart) {
        this.meetingStart = meetingStart;
    }

    public LocalDateTime getMeetingEnd() {
        return this.meetingEnd;
    }

    public void setMeetingEnd(LocalDateTime meetingEnd) {
        this.meetingEnd = meetingEnd;
    }

    public List<Account> getMeetingParticipants() {
        return this.meetingParticipants;
    }

    public void setMeetingParticipants(List<Account> meetingParticipants) {
        this.meetingParticipants = meetingParticipants;
    }

    public boolean containsAccountID(int accountID){
        for(Account account : meetingParticipants){
            if(account.getAccountID() == accountID){
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "{" +
            " meetingID='" + getMeetingID() + "'" +
            ", meetingName='" + getMeetingName() + "'" +
            ", meetingDescription='" + getMeetingDescription() + "'" +
            ", meetingCreatorID='" + getMeetingCreatorID() + "'" +
            ", meetingStart='" + getMeetingStart() + "'" +
            ", meetingEnd='" + getMeetingEnd() + "'" +
            ", meetingParticipants='" + getMeetingParticipants() + "'" +
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
        
        Meeting meeting = (Meeting)obj;

        if(
            meetingID == meeting.getMeetingID()
            && meetingName.equals(meeting.getMeetingName())
            && meetingDescription.equals(meeting.getMeetingDescription())
            && meetingCreatorID == meeting.getMeetingCreatorID()
            && meetingStart.equals(meeting.getMeetingStart())
            && meetingEnd.equals(meeting.getMeetingEnd())
            && meetingParticipants.equals(meeting.getMeetingParticipants())
        ){
            return true;
        }

        return false;
    }
    


}
