package com.noagility.personalcrm.controller;


import com.noagility.personalcrm.Util.JwtTokenUtil;
import com.noagility.personalcrm.model.*;
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
    private JwtTokenUtil jwtTokenUtil;

    //add a contact to the logged in users account
    @RequestMapping(
            value = "/create",
            method = RequestMethod.POST
    )
    public ResponseEntity<String> create(@RequestBody Map<String, Object> payload, @CookieValue("jwt") String token) throws Exception{
        if(contactService.addContact(
                jwtTokenUtil.getAccountFromToken(token).getAccountID(),
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
            method = RequestMethod.GET
    )
    public ResponseEntity<List<Contact>> read(@CookieValue("jwt") String token){
        return ResponseEntity.ok().body(
                contactService.getContacts(
                        jwtTokenUtil.getAccountFromToken(token).getAccountID()));
    }

    //currently useless given cannot change info
    @RequestMapping(
            value = "/update",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> update(@RequestBody Map<String, Object> payload, @CookieValue("jwt") String token){

        if (contactService.updateContact(
                jwtTokenUtil.getAccountFromToken(token).getAccountID(),
                (Integer)payload.get("contactID"),
                (String)payload.getOrDefault("contactEmail", null),
                (String)payload.getOrDefault("contactAddress", null),
                (String)payload.getOrDefault("contactPhone", null),
                (String)payload.getOrDefault("contactJobTitle", null),
                (String)payload.getOrDefault("contactCompany", null)
        )) {
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
    public ResponseEntity<String> delete(@RequestBody Map<String, Object> payload, @CookieValue("jwt") String token) throws Exception {
        if(contactService.deleteContact(
                jwtTokenUtil.getAccountFromToken(token).getAccountID(),
                (String)payload.get("contact")
        )){
            return ResponseEntity.ok().body("Success");
        }
        return ResponseEntity.badRequest().body("Failure");
    }


}
