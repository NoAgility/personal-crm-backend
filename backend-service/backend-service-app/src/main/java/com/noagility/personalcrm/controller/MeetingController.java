package com.noagility.personalcrm.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.noagility.personalcrm.Util.JwtTokenUtil;
import com.noagility.personalcrm.model.Account;
import com.noagility.personalcrm.model.Meeting;
import com.noagility.personalcrm.service.MeetingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/meeting")
public class MeetingController {

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    MeetingService meetingService;

    @RequestMapping(
        value = "/createMeeting",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> createMeeting(@RequestBody Map<String, Object> payload, @CookieValue("jwt") String token){
        try{
            List<Integer> participants = (List<Integer>)payload.get("accountIDs");
            Account account = jwtTokenUtil.getAccountFromToken(token);
            participants.add(account.getAccountID());
            Set<Integer> set = new HashSet<>(participants);
            participants.clear();
            participants.addAll(set);

            if(
                participants.contains(account.getAccountID())
                && meetingService.createMeeting(
                    participants,
                    account.getAccountID(),
                    (String)payload.get("meetingName"), 
                    (String)payload.get("meetingDescription"), 
                    (String)payload.get("meetingStart"), 
                    (String)payload.get("meetingEnd")
                )
            ){
                return ResponseEntity.ok().body("Sucess");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().body("Failure");
    }

    @RequestMapping(
        value = "/editMeeting",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> editMeeting(@RequestBody Map<String, Object> payload, @CookieValue("jwt") String token){
        try{
            if(
                meetingService.validateMeetingCreator(
                    token, 
                    (int)payload.get("meetingID")
                )
                && meetingService.editMeeting(
                    (int)payload.get("meetingID"), 
                    (String)payload.get("meetingName"), 
                    (String)payload.get("meetingDescription"), 
                    (String)payload.get("meetingStart"), 
                    (String)payload.get("meetingEnd)")
                )
            ){
                return ResponseEntity.ok().body("Success");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().body("Failure");
    }

    @RequestMapping(
        value = "/deleteMeeting",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> deleteMeeting(@RequestBody Map<String, Object> payload, @CookieValue("jwt") String token){
        try{
            if(
                meetingService.validateMeetingCreator(
                    token, 
                    (int)payload.get("meetingID")
                )
                && meetingService.deleteMeeting(
                    (int)payload.get("meetingID")
                )
            ){
                return ResponseEntity.ok().body("Success");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().body("Failure");
    }

    @RequestMapping(
        value = "/getMeetingByID",
        method = RequestMethod.GET
    )
    public ResponseEntity<Meeting> editMeeting(@RequestParam int meetingID, @CookieValue("jwt") String token){
        try{
            if(
                meetingService.validateMeetingParticipant(token, meetingID)
            ){
                return ResponseEntity.ok().body(meetingService.getMeetingByID(meetingID));
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().body(null);
    }

    @RequestMapping(
        value = "/getAccountMeetings",
        method = RequestMethod.GET
    )
    public ResponseEntity<List<Meeting>> getAccountMeetings(@CookieValue("jwt") String token){
        try{
            Account account = jwtTokenUtil.getAccountFromToken(token);
            return ResponseEntity.ok().body(meetingService.getAccountMeetingsByID(account.getAccountID()));
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().body(null);
    }

    @RequestMapping(
        value = "/createMinute",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> createMinute(@RequestBody Map<String, Object> payload, @CookieValue("jwt") String token){
        try{
            if(
                meetingService.validateMeetingParticipant(
                    token, 
                    (int)payload.get("meetingID")
                )
                && meetingService.createMinute(
                    (int)payload.get("meetingID"), 
                    (String)payload.get("minuteText")
                )
            ){
                return ResponseEntity.ok().body("Success");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().body("Failure");
    }

    @RequestMapping(
        value = "/deleteMinute",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> deleteMinute(@RequestBody Map<String, Object> payload, @CookieValue("jwt") String token){
        try{
            if(
                meetingService.validateMeetingParticipant(
                    token, 
                    (int)payload.get("meetingID")
                )
                && meetingService.validateMinuteCreator(
                    token, 
                    (int)payload.get("meetingID"), 
                    (int)payload.get("minuteID")
                )
                && meetingService.deleteMinute(
                    (int)payload.get("meetingID"), 
                    (int)payload.get("minuteID")
                )
            ){
                return ResponseEntity.ok().body("Success");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().body("Failure");
    }

    @RequestMapping(
        value = "/editMinute",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> editMinute(@RequestBody Map<String, Object> payload, @CookieValue("jwt") String token){
        try{
            if(
                meetingService.validateMeetingParticipant(
                    token, 
                    (int)payload.get("meetingID")
                )
                && meetingService.validateMinuteCreator(
                    token, 
                    (int)payload.get("meetingID"), 
                    (int)payload.get("minuteID")
                )
                && meetingService.editMinute(
                    (int)payload.get("meetingID"), 
                    (int)payload.get("minuteID"), 
                    (String)payload.get("minuteText")
                )
            ){
                return ResponseEntity.ok().body("Success");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().body("Failure");
    }

    @RequestMapping(
        value = "/acceptMeeting",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> acceptMeeting(@RequestBody Map<String, Object> payload, @CookieValue("jwt") String token){
        try{
            Account account = jwtTokenUtil.getAccountFromToken(token);

            if(
                meetingService.validateMeetingParticipant(
                    token, 
                    (int)payload.get("meetingID")
                )
                && meetingService.acceptMeeting(
                    (int)payload.get("meetingID"), 
                    account.getAccountID()
                )
            ){
                return ResponseEntity.ok().body("Success");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().body("Failure");
    }

    @RequestMapping(
        value = "/declineMeeting",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> declineMeeting(@RequestBody Map<String, Object> payload, @CookieValue("jwt") String token){
        try{
            Account account = jwtTokenUtil.getAccountFromToken(token);

            if(
                meetingService.validateMeetingParticipant(
                    token, 
                    (int)payload.get("meetingID")
                )
                && meetingService.declineMeeting(
                    (int)payload.get("meetingID"), 
                    account.getAccountID()
                )
            ){
                return ResponseEntity.ok().body("Success");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().body("Failure");
    }
}
