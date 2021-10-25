package com.noagility.personalcrm.controller;


import com.noagility.personalcrm.model.*;
import com.noagility.personalcrm.service.AccountService;
import com.noagility.personalcrm.service.ContactService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    ContactService contactService;

    /**
     * API endpoint to create an account
     * @param payload The payload of the request containing the account details
     * @param referral An optional request parameter indicating that the registration was through referral
     * @return A ResponseEntity indicating the success of the request
     * @throws Exception Indicates that the server failed to create an account for the client
     */
    @RequestMapping(
        value = "/create",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> create(@RequestBody Map<String, Object> payload, @RequestParam(value="referral", defaultValue = "") String referral) throws Exception{
        if(accountService.registerAccount(
            (String)payload.get("username"), 
            (String)payload.get("password"),
            (String)payload.get("name"),
            (String)payload.get("dob")
        )){
            if(referral != "" && accountService.getByUsername(referral) != null){
                contactService.addContactWithUsername((String)payload.get("username"), referral);
            }
            return ResponseEntity.ok().body("Success");
        }
        return ResponseEntity.badRequest().body("Failure");
    }

    /**
     * API endpoint to deactivate an account
     * @param payload The payload containing the id of the account to deactivate
     * @return A ResponseEntity indicating the success of the request
     * @throws Exception Indicates that the server failed in deactivating an account for the client
     */
    @RequestMapping(
        value = "/deactivate",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> deactivateAccount(@RequestBody Map<String, Object> payload) throws Exception {
        if(accountService.deactivateAccount(
            (int)payload.get("id")
        )){
            return ResponseEntity.ok().body("Success");
        }
        return ResponseEntity.badRequest().body("Failure");
    }

    /**
     * API endpoint to get an account by username
     * @param username The username of the account to fetch
     * @return A ResponseEntity containing the account details
     */
    @RequestMapping(
        value = "/get",
        method = RequestMethod.GET,
        params = {
            "username"
        }
    )
    public ResponseEntity<Account> getAccountByUsername(@RequestParam String username){
        return ResponseEntity.ok().body(accountService.getByUsername(username));
    }

    @RequestMapping(
            value = "/test",
            method = RequestMethod.GET
    )
    public ResponseEntity<String> getAccountByUsername(){
        return ResponseEntity.ok().body("test");
    }

    /**
     * API endpoint to get an account by id
     * @param id The id of the account to fetch
     * @return A ResponseEntity containing the account details
     */
    @RequestMapping(
        value = "/get",
        method = RequestMethod.GET,
        params = {
            "id"
        }
    )
    public ResponseEntity<Account> getAccountByID(@RequestParam int id){
        return ResponseEntity.ok().body(accountService.getByID(id));
    }




}
