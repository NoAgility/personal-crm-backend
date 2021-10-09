package com.noagility.personalcrm.service;

package com.noagility.personalcrm.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import com.noagility.personalcrm.Util.JwtTokenUtil;
import com.noagility.personalcrm.mapper.AccountRowMapper;
import com.noagility.personalcrm.mapper.ChatRowMapper;
import com.noagility.personalcrm.mapper.IntegerRowMapper;
import com.noagility.personalcrm.mapper.MeetingRowMapper;
import com.noagility.personalcrm.mapper.MessageRowMapper;
import com.noagility.personalcrm.mapper.MinuteRowMapper;
import com.noagility.personalcrm.model.Account;
import com.noagility.personalcrm.model.Chat;
import com.noagility.personalcrm.model.Meeting;
import com.noagility.personalcrm.model.Message;
import com.noagility.personalcrm.model.Minute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;

public class MeetingService {
    @Autowired
    DataSource dataSource;

    @Autowired 
    JdbcTemplate jdbcTemplate;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    IntegerRowMapper integerRowMapper;

    @Autowired
    MeetingRowMapper meetingRowMapper;

    @Autowired
    MinuteRowMapper minuteRowMapper;


    private int maxMeetingID;

    @EventListener(ApplicationReadyEvent.class)
    private void loadMeetingID(){
        String sql = "SELECT MAX(MeetingID) as MeetingID FROM Meetings";

        try{
            maxMeetingID = jdbcTemplate.queryForObject(sql, Integer.class);
        }
        catch(Exception e){
            maxMeetingID = 0;
        }
    }

    public boolean createMeeting(Collection<Integer> accountIDs, int meetingCreatorID, String meetingName, String meetingDescription, String meetingStart, String meetingEnd){
        System.out.println(String.format("createMeeting: {\"accountIDs\": %s, \"meetingCreatorID\": %d, \"meetingName\": \"%s\", \"meetingDescription\": \"%s\", \"meetingStart\": %s, \"meetingEnd\": %s", accountIDs.toString(), meetingCreatorID, meetingName, meetingDescription, meetingStart, meetingEnd));
        try{
            //  Insert new meeting into Meetings table
            String sql = "INSERT INTO Meetings(MeetingID, MeetingName, MeetingDescription, MeetingCreatorID, MeetingStart, MeetingEnd) VALUES (?, ?)";
            int meetingID = ++maxMeetingID;
            jdbcTemplate.update(sql, meetingID, meetingName, meetingDescription, meetingCreatorID, meetingStart, meetingEnd);

            //  Link each accound to the new meeting
            sql = "INSERT INTO Accounts_Meetings(AccountID, MeetingID) VALUES (?, ?)";
            List<Object[]> rows = new ArrayList<>();

            for(int accountID : accountIDs){
                rows.add(new Object[] {accountID, meetingID});
            }

            jdbcTemplate.batchUpdate(sql, rows);

            return true;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public boolean editMeeting(int meetingID, String meetingName, String meetingDescription, String meetingStart, String meetingEnd){
        System.out.println(String.format("editMeeting: {\"meetingID\": %d, \"meetingName\": \"%s\", \"meetingDescription\": \"%s\", \"meetingStart\": %s, \"meetingEnd\": %s", meetingID, meetingName, meetingDescription, meetingStart, meetingEnd));
        try{
            //  Edit message by meetingID
            String sql = "UPDATE Meetings SET MeetingName = ?, MeetingDescription = ?, MeetingStart = ?, MeetingEnd = ? WHERE MeetingID = ?";
            jdbcTemplate.update(sql, meetingName, meetingDescription, meetingStart, meetingEnd, meetingID);

            return true;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteMeeting(int meetingID){
        System.out.println(String.format("deleteMeeting: {\"meetingID\": %d}", meetingID));
        try{
            //  Delete meeting by meetingID
            String sql = "DELETE FROM Meetings WHERE MeetingID = ?";
            jdbcTemplate.update(sql, meetingID);

            return true;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public Meeting getMeetingByID(int meetingID){
        System.out.println(String.format("getMeetingByID: {\"meetingID\": %d}", meetingID));
        try{
            //  Get meeting by meetingID
            String sql = "SELECT * FROM Meetings WHERE MeetingID = ?";
            Meeting meeting = jdbcTemplate.queryForObject(sql, meetingRowMapper, meetingID);

            //  Get minutes by meetingID
            List<Minute> minutes = getMeetingMinutes(meetingID);
            meeting.setMeetingMinutes(minutes);

            //  Get accountIDs by meetingID
            List<Integer> accountIDs = getMeetingParticipantIDs(meetingID);
            meeting.setMeetingParticipants(accountIDs);

            return meeting;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public List<Meeting> getAccountMeetingsByID(int accountID){
        System.out.println(String.format("getAccountMeetingsByID: {\"accountID\": %d}", accountID));
        try{
            //  Get all MeetingIDs for account
            String sql = "SELECT MeetingID AS `Integer` FROM Accounts_Meetings WHERE AccountID = ?";
            List<Integer> meetingIDs = jdbcTemplate.query(sql, integerRowMapper, accountID);

            if(meetingIDs == null){
                return null;
            }

            List<Meeting> meetings = new ArrayList<>();

            for(int meetingID : meetingIDs){
                Meeting meeting = getMeetingByID(meetingID);

                if(meeting != null){
                    meetings.add(meeting);
                }
            }

            return meetings;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public boolean createMinute()

    public Minute getMinuteByID(int meetingID, int minuteID){
        System.out.println(String.format("getMinuteByID: {\"meetingID\": %d, \"minuteID\": %d}", meetingID, minuteID));
        try{
            //  Get minute by meetingID and minuteID
            String sql = "SELECT * FROM Minutes WHERE MeetingID = ? AND MinuteID = ?";
            Minute minute = jdbcTemplate.queryForObject(sql, minuteRowMapper, meetingID, minuteID);
            
            return minute;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public List<Minute> getMeetingMinutes(int meetingID){
        System.out.println(String.format("getMeetingMinutes: {\"meetingID\": %d}"));
        try{
            //  Get minutes by meetingID
            String sql = "SELECT * FROM Minutes WHERE MeetingID = ? ORDER BY MinuteCreation ASC";
            List<Minute> minutes = jdbcTemplate.query(sql, minuteRowMapper, meetingID);

            return minutes;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public List<Integer> getMeetingParticipantIDs(int meetingID){
        System.out.println(String.format("getMeetingParticipantIDs: {\"meetingID\": %d}", meetingID));
        try{
            //  Get accoundIDs by meetingID
            String sql = "SELECT AccountID AS `Integer` FROM Accounts_Meetings WHERE MeetingID = ?";
            List<Integer> meetingParticipantIDs = jdbcTemplate.query(sql, integerRowMapper, meetingID);

            return meetingParticipantIDs;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public boolean validateMeetingCreator(String token, int meetingID){
        System.out.println(String.format("validateMeetingCreator: {\"token\": \"%s\", \"meetingID\": %d}", token, meetingID));
        try{
            Meeting meeting = getMeetingByID(meetingID);
            return jwtTokenUtil.validateTokenSender(token, meeting.getMeetingCreatorID());
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public boolean validateMinuteCreator(String token, int meetingID, int minuteID){

    }
}
