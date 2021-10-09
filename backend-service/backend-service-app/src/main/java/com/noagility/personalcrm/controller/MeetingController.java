package com.noagility.personalcrm.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.noagility.personalcrm.Util.JwtTokenUtil;
import com.noagility.personalcrm.model.Account;
import com.noagility.personalcrm.model.Chat;
import com.noagility.personalcrm.service.ChatService;
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
                && meetingService.createMeeting()
            ){
                return ResponseEntity.ok().body("Sucess");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().body("Failure");
    }
}
