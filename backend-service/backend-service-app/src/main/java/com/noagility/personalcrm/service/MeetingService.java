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

    /**
     * Method on application start, reads from the database the current max ID and increments it to insert future
     * entries
     */
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

    /**
     * Method to create a meeting
     * @param accountIDs The ids of the accounts that will participate in the meeting
     * @param meetingCreatorID The id of the account that created the meeting
     * @param meetingName The name of the meeting
     * @param meetingDescription The description of the meeting
     * @param meetingStart The start date/time of the meeting
     * @param meetingEnd The end date/time of the meeting
     * @return a boolean indiciating the success of the transaction
     */
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

    /**
     * Method to edit a meeting
     * @param meetingName The name of the meeting
     * @param meetingDescription The description of the meeting
     * @param meetingStart The start date/time of the meeting
     * @param meetingEnd The end date/time of the meeting
     * @return a boolean indiciating the success of the transaction
     */
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

    /**
     * Method to delete a meeting
     * @param meetingID The id of the meeting to delete
     * @return a boolean indicating the success of the transaction
     */
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

    /**
     * Method to get a meeting by it's id
     * @param meetingID The id of the meeting to fetch
     * @return A meeting object
     */
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

    /**
     * Method to fetch all the meetings for an account
     * @param accountID The id of the account
     * @return A List of meeting objects
     */
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

    /**
     * Method to create a minute for a meeting
     * @param meetingID The id of the meeting to create a minute for
     * @param minuteText The text of the minute
     * @param accountID The id of the account creating the minute
     * @return a boolean indiciating the success of the transaction
     */
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

    /**
     * Method to edit a minute for a meeting
     * @param meetingID The id of the meeting
     * @param minuteID The id of the minute
     * @param minuteText The new minute text
     * @return a boolean indiciating the success of the transaction
     */
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

    /**
     * Method to delete a minute from a meeting
     * @param meetingID The id of the meeting
     * @param minuteID The id of the minute
     * @return a boolean indiciating the success of the transaction
     */
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

    /**
     * Method to mark a meeting as accepted by an account
     * @param meetingID The id of the meeting
     * @param accountID The id of the account
     * @return a boolean indicating the success of the transaction
     */
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

    /**
     * Method to mark a meeting as declined by an account
     * @param meetingID The id of the meeting
     * @param accountID The id of the account
     * @return a boolean indicating the success of the transaction
     */
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

    /**
     * Method to get a minute object by id
     * @param meetingID The id of the meeting that contains the minute
     * @param minuteID The id of the minute
     * @return a boolean indicating the success of the transaction
     */
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

    /**
     * Method to get all the minutes of a meeting
     * @param meetingID The meeting to get the minutes for
     * @return A List of minute objects
     */
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

    /**
     * Method to get all the participants of a meeting
     * @param meetingID The id of the meeting
     * @return A Map from (Integer, Boolean) -> (AccountID, Coming?)
     */
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

    /**
     * Method to validate if the owner of a token is the creator of a meeting
     * @param token The JWT token sent
     * @param meetingID The id of the meeting
     * @return a boolean indicating if the owner of the token created the meeting
     */
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

    /**
     * Method to validate if the owner of a token is the creator of a minute
     * @param token The JWT token sent
     * @param meetingID The id of the minute
     * @return a boolean indicating if the owner of the token created the minute
     */
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

    /**
     * Method to validate if the owner of a token is a participant of a meeting
     * @param token The JWT token sent
     * @param meetingID The id of the meeting
     * @return a boolean indicating if the owner of the token is a participant
     */
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

    /**
     * Method to validate if the owner of a token has accepted a meeting
     * @param token The JWT token sent
     * @param meetingID The id of the meeting
     * @return a boolean indicating if the owner of the token has accepted
     */
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
