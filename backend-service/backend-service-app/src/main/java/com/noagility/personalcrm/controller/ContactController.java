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


    /**
     * API endpoint to create a contact for a user from another account
     * @param payload The payload of the request containing the contact username
     * @param token The JWT token used to authenticate the request and indicate the owner
     * @return A ResponseEntity indicating the success of the request
     * @throws Exception Indicates that the server failed to create the contact for the user
     */
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

    /**
     * API endpoint to fetch all the contacts of an account based on their token
     * @param token The JWT token used to authenticate the request and fetch the accounts
     * @return A ResponseEntity containing the contacts and their details
     */
    @RequestMapping(
            value = "/read",
            method = RequestMethod.GET
    )
    public ResponseEntity<List<Contact>> read(@CookieValue("jwt") String token){
        return ResponseEntity.ok().body(
                contactService.getContacts(
                        jwtTokenUtil.getAccountFromToken(token).getAccountID()));
    }

    /**
     * API endpoint to update the custom information of a contact
     * @param payload The payload containing the contact details to be updated with
     * @param token The JWT token used to authenticate the request
     * @return A ResponseEntity indicating the success of the request
     */
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

    /**
     * API endpoint to delete a contact from an account
     * @param payload The payload of the request containing the username of the contact to delete
     * @param token The JWT token used to authenticate the request
     * @return A ResponseEntity indicating the success of the request
     * @throws Exception Indicates that an issue has occurred in deleting a contact for a user
     */
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
