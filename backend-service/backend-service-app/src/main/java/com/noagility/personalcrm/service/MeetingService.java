package com.noagility.personalcrm.service;

import java.util.*;

import javax.sql.DataSource;

import com.noagility.personalcrm.Util.JwtTokenUtil;
import com.noagility.personalcrm.mapper.IntegerRowMapper;
import com.noagility.personalcrm.mapper.MeetingRowMapper;
import com.noagility.personalcrm.mapper.MinuteRowMapper;
import com.noagility.personalcrm.model.Account;
import com.noagility.personalcrm.model.Meeting;
import com.noagility.personalcrm.model.Minute;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
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

    @Autowired
    TinyintMapperService tinyintMapperService;

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
        log.info("TaskService has started, incrementing MeetingID from {}", maxMeetingID);
    }

    public boolean createMeeting(Collection<Integer> accountIDs, int meetingCreatorID, String meetingName, String meetingDescription, String meetingStart, String meetingEnd){
        try{
            //  Insert new meeting into Meetings table
            String sql = "INSERT INTO Meetings(MeetingID, MeetingName, MeetingDescription, MeetingCreatorID, MeetingStart, MeetingEnd) VALUES (?, ?, ?, ?, ?, ?)";
            int meetingID = ++maxMeetingID;
            jdbcTemplate.update(sql, meetingID, meetingName, meetingDescription, meetingCreatorID, meetingStart, meetingEnd);

            //  Link each accound to the new meeting
            sql = "INSERT INTO Accounts_Meetings(AccountID, MeetingID, Accounts_MeetingsAccepted) VALUES (?, ?, ?)";
            List<Object[]> rows = new ArrayList<>();

            for(int accountID : accountIDs){
                rows.add(new Object[] {accountID, meetingID, accountID == meetingCreatorID ? true : false});
            }

            jdbcTemplate.batchUpdate(sql, rows);
            log.info("Meeting created for accounts {}, MeetingID: {}", Arrays.toString(accountIDs.toArray()), meetingID);
            return true;
        }
        catch(Exception e){
            log.error("Failed to create meeting for account {} with details: Name={}, Desc={}, Start={}, End={}, Participants={}",
                    meetingCreatorID, meetingName, meetingDescription, meetingStart, meetingEnd, Arrays.toString(accountIDs.toArray()), e);
        }

        return false;
    }

    public boolean editMeeting(int meetingID, String meetingName, String meetingDescription, String meetingStart, String meetingEnd){
        try{
            //  Edit message by meetingID
            String sql = "UPDATE Meetings SET MeetingName = ?, MeetingDescription = ?, MeetingStart = ?, MeetingEnd = ? WHERE MeetingID = ?";
            jdbcTemplate.update(sql, meetingName, meetingDescription, meetingStart, meetingEnd, meetingID);
            log.info("Meeting {} edited, new meeting values: Name={}, Desc={}, Start={}, End={}", meetingID, meetingName, meetingDescription, meetingStart, meetingEnd);
            return true;
        }
        catch(Exception e){
            log.error("Failed to edit meeting (id: {})", meetingID, e);
        }

        return false;
    }

    public boolean deleteMeeting(int meetingID){
        try{
            //  Delete minutes by meetingID
            String sql = "DELETE FROM Minutes WHERE MeetingID = ?";
            jdbcTemplate.update(sql, meetingID);
            //  Delete accounts_meetings by meetingID
            sql = "DELETE FROM Accounts_Meetings WHERE MeetingID = ?";
            jdbcTemplate.update(sql, meetingID);
            //  Delete meeting by meetingID
            sql = "DELETE FROM Meetings WHERE MeetingID = ?";
            jdbcTemplate.update(sql, meetingID);
            log.info("Meeting {} deleted", meetingID);
            return true;
        }
        catch(Exception e){
            log.error("Failed to delete meeting (id: {})", meetingID, e);
        }

        return false;
    }

    public Meeting getMeetingByID(int meetingID){
        try{
            //  Get meeting by meetingID
            String sql = "SELECT * FROM Meetings WHERE MeetingID = ?";
            Meeting meeting = jdbcTemplate.queryForObject(sql, meetingRowMapper, meetingID);

            //  Get minutes by meetingID
            List<Minute> minutes = getMeetingMinutes(meetingID);
            meeting.setMeetingMinutes(minutes);

            //  Get accountIDs by meetingID
            Map<Integer, Boolean> accountIDs = getMeetingParticipants(meetingID);
            meeting.setMeetingParticipants(accountIDs);

            return meeting;
        }
        catch(Exception e){
            log.error("Failed to fetch meeting by id {}", meetingID, e);
        }

        return null;
    }

    public List<Meeting> getAccountMeetingsByID(int accountID){
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
            log.error("Failed to fetch meeting of account (id: {})", accountID, e);
        }

        return null;
    }

    public boolean createMinute(int meetingID, String minuteText, int accountID){
        try{
            //  Insert new minute to Minutes table
            String sql = "INSERT INTO Minutes(MeetingID, MinuteText, AccountID) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, meetingID, minuteText, accountID);
            log.info("Meeting Minute \"{}\" created for meeting (id: {})", minuteText, meetingID);
            return true;
        }
        catch(Exception e){
            log.error("Failed to create minute for meeting (id: {})", meetingID, e);
        }

        return false;
    }

    public boolean editMinute(int meetingID, int minuteID, String minuteText){
        try{
            //  Edit minute by meetingID and minuteID
            String sql = "UPDATE Minutes SET MinuteText = ? WHERE MeetingID = ? AND MinuteID = ?";
            jdbcTemplate.update(sql, minuteText, meetingID, minuteID);
            log.info("Meeting Minute {} edited for meeting (id: {}), new minute=\"{}\"", minuteID, meetingID, minuteText);
            return true;
        }
        catch(Exception e){
            log.error("Failed to edit minute (id: {}) of meeting (id: {})", minuteID, meetingID, e);
        }

        return false;
    }

    public boolean deleteMinute(int meetingID, int minuteID){

        try{
            //  Delete minute by meetingID and minuteID
            String sql = "DELETE FROM Minutes WHERE MeetingID = ? AND MinuteID = ?";
            jdbcTemplate.update(sql, meetingID, minuteID);
            log.info("Meeting Minute (id: {}) deleted for meeting (id: {})", minuteID, meetingID);
            return true;
        }
        catch(Exception e){
            log.error("Failed to delete minute (id: {}) of meeting (id: {})", minuteID, meetingID, e);
        }

        return false;
    }

    public boolean acceptMeeting(int meetingID, int accountID){

        try{
            String sql = "UPDATE Accounts_Meetings SET Accounts_MeetingsAccepted = 1 WHERE MeetingID = ? AND AccountID = ? AND Accounts_MeetingsAccepted = 0";

            if(jdbcTemplate.update(sql, meetingID, accountID) > 0){
                log.info("Meeting (id: {}) accepted by account {}", meetingID, accountID);
                return true;
            }
        }
        catch(Exception e){
            log.error("Failed to accept meeting (id: {}) from account (id: {})", meetingID, accountID, e);
        }

        return false;
    }

    public boolean declineMeeting(int meetingID, int accountID){
        try{
            String sql = "DELETE FROM Accounts_Meetings WHERE MeetingID = ? AND AccountID = ? AND Accounts_MeetingsAccepted = 0";

            if(jdbcTemplate.update(sql, meetingID, accountID) > 0){
                log.info("Meeting (id: {}) declined by account (id: {})", meetingID, accountID);
                return true;
            }
        }
        catch(Exception e){
            log.error("Failed to decline meeting (id: {}) from account (id: {})", meetingID, accountID, e);
        }

        return false;
    }

    public Minute getMinuteByID(int meetingID, int minuteID){
        try{
            //  Get minute by meetingID and minuteID
            String sql = "SELECT * FROM Minutes WHERE MeetingID = ? AND MinuteID = ?";
            Minute minute = jdbcTemplate.queryForObject(sql, minuteRowMapper, meetingID, minuteID);
            
            return minute;
        }
        catch(Exception e){
            log.error("Failed to fetch minute (id: {}) for meeting (id: {})", minuteID, meetingID, e);
        }

        return null;
    }

    public List<Minute> getMeetingMinutes(int meetingID){
        try{
            //  Get minutes by meetingID
            String sql = "SELECT * FROM Minutes WHERE MeetingID = ? ORDER BY MinuteCreation ASC";
            List<Minute> minutes = jdbcTemplate.query(sql, minuteRowMapper, meetingID);

            return minutes;
        }
        catch(Exception e){
            log.error("Failed to fetch minutes for meeting (id: {})", meetingID, e);
        }

        return null;
    }

    public Map<Integer, Boolean> getMeetingParticipants(int meetingID){
        try{
            //  Get accoundIDs by meetingID
            String sql = "SELECT DISTINCT AccountID, Accounts_MeetingsAccepted FROM Accounts_Meetings WHERE MeetingID = ?";
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, meetingID);
            Map<Integer, Boolean> meetingParticipants = new HashMap<>();

            for(Map<String, Object> m : results){
                meetingParticipants.put((int)m.get("AccountID"), tinyintMapperService.map(m.get("Accounts_MeetingsAccepted")));
            }
            return meetingParticipants;
        }
        catch(Exception e){
            log.error("Failed to fetch participants for meeting (id: {})", meetingID, e);
        }

        return null;
    }

    public boolean validateMeetingCreator(String token, int meetingID){
        try{
            Meeting meeting = getMeetingByID(meetingID);
            return jwtTokenUtil.validateTokenSender(token, meeting.getMeetingCreatorID());
        }
        catch(Exception e){
            log.error("Failed to validate meeting creator for meeting (id: {})", meetingID);
        }

        return false;
    }

    public boolean validateMinuteCreator(String token, int meetingID, int minuteID){
        try{
            Minute minute = getMinuteByID(meetingID, minuteID);
            return jwtTokenUtil.validateTokenSender(token, minute.getAccountID());
        }
        catch(Exception e){
            log.error("Failed to validate minute creator for meeting (id: {})", minuteID, e);
        }

        return false;
    }

    public boolean validateMeetingParticipant(String token, int meetingID){
        try{
            Meeting meeting = getMeetingByID(meetingID);
            Account account = jwtTokenUtil.getAccountFromToken(token);
            return meeting.containsAccountID(account.getAccountID());
        }
        catch(Exception e){
            log.error("Failed to validate meeting participant for meeting (id: {})", meetingID, e);
        }

        return false;
    }

    public boolean validateMeetingAccepted(String token, int meetingID){
        try{
            Meeting meeting = getMeetingByID(meetingID);
            Account account = jwtTokenUtil.getAccountFromToken(token);
            return meeting.containsAccountID(account.getAccountID()) && meeting.getMeetingParticipants().get(account.getAccountID()) == true;
        }
        catch(Exception e){
            log.error("Failed to validate meeting accepted for meeting (id: {})", meetingID, e);
        }

        return false;
    }
}
