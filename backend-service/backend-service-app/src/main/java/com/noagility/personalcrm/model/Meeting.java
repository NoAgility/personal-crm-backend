package com.noagility.personalcrm.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Meeting {
    private int meetingID;
    private String meetingName;
    private String meetingDescription;
    private int meetingCreatorID;
    private LocalDateTime meetingStart;
    private LocalDateTime meetingEnd;
    private LocalDateTime meetingCreation;
    private Map<Integer, Boolean> meetingParticipants;
    private List<Minute> meetingMinutes;

    public Meeting(int meetingID, String meetingName, String meetingDescription, int meetingCreatorID, LocalDateTime meetingStart, LocalDateTime meetingEnd, LocalDateTime meetingCreation){
        this.meetingID = meetingID;
        this.meetingName = meetingName;
        this.meetingDescription = meetingDescription;
        this.meetingCreatorID = meetingCreatorID;
        this.meetingStart = meetingStart;
        this.meetingEnd = meetingEnd;
        this.meetingCreation = meetingCreation;
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

    public LocalDateTime getMeetingCreation() {
        return this.meetingCreation;
    }

    public void setMeetingCreation(LocalDateTime meetingCreation) {
        this.meetingCreation = meetingCreation;
    }

    public Map<Integer, Boolean> getMeetingParticipants() {
        return this.meetingParticipants;
    }

    public void setMeetingParticipants(Map<Integer, Boolean> meetingParticipants) {
        this.meetingParticipants = meetingParticipants;
    }

    public List<Minute> getMeetingMinutes() {
        return this.meetingMinutes;
    }

    public void setMeetingMinutes(List<Minute> meetingMinutes) {
        this.meetingMinutes = meetingMinutes;
    }

    public boolean containsAccountID(int accountID){
        return meetingParticipants.keySet().contains(accountID);
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
            ", meetingCreation='" + getMeetingCreation() + "'" +
            ", meetingParticipants='" + getMeetingParticipants() + "'" +
            ", meetingMinutes='" + getMeetingMinutes() + "'" +
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
            && meetingCreation.equals(meeting.getMeetingCreation())
            && meetingParticipants.equals(meeting.getMeetingParticipants())
            && meetingMinutes.equals(meeting.getMeetingMinutes())
        ){
            return true;
        }

        return false;
    }
    


}
