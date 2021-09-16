package com.noagility.personalcrm.controller;


import com.noagility.personalcrm.Util.JwtTokenUtil;
import com.noagility.personalcrm.model.*;
import com.noagility.personalcrm.service.AccountService;
import com.noagility.personalcrm.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/contact")
public class ContactController {
    @Autowired
    private ContactService contactService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    //add a contact to the logged in users account
    @RequestMapping(
            value = "/create",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> create(@RequestBody Map<String, Object> payload) throws Exception{
        if(contactService.addContact(
                jwtTokenUtil.getUsernameFromToken((String)payload.get("token")),
                (String)payload.get("contact")
        )){
            return ResponseEntity.ok().body("Success");
        }
        return ResponseEntity.badRequest().body("Failure");
    }

    //get all contacts for account by username, figure out how to exclude id later or just do in front end
//    @RequestMapping(
//            value = "/read/single",
//            method = RequestMethod.GET,
//            params = {
//                    "username"
//            }
//    )
//    public ResponseEntity<List<Account>> read(@RequestParam String username){
//        return ResponseEntity.ok().body(accountService.getAccounts(username));
//    }
    //get all contacts for account by username, figure out how to exclude id later or just do in front end
    @RequestMapping(
            value = "/read",
            method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<Contact>> read(@RequestBody Map<String, Object> payload){
        return ResponseEntity.ok().body(
                contactService.getContacts(
                        jwtTokenUtil.getUsernameFromToken((String)payload.get("token"))));
    }

    //currently useless given cannot change info
    @RequestMapping(
            value = "/update",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> update(@RequestBody Map<String, Object> payload){

        if(contactService.updateContact(
                jwtTokenUtil.getUsernameFromToken((String)payload.get("token"))
        )){
            return ResponseEntity.ok().body("Success");
        }
        return ResponseEntity.badRequest().body("Failure");
    }

    //currently just deletes the contact from the database may be subject to change
    @RequestMapping(
            value = "/delete",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> delete(@RequestBody Map<String, Object> payload) throws Exception {
        if(contactService.deleteContact(
                jwtTokenUtil.getUsernameFromToken((String)payload.get("token")),
                (String)payload.get("contact")
        )){
            return ResponseEntity.ok().body("Success");
        }
        return ResponseEntity.badRequest().body("Failure");
    }


}
