package com.noagility.personalcrm.controller;


import com.noagility.personalcrm.model.*;
import com.noagility.personalcrm.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/account")
@CrossOrigin(origins="http://localhost:3000")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/dburl")
    public ResponseEntity<String> getDbPort() {
        return ResponseEntity.ok().body(System.getenv("SPRING_DATASOURCE_URL"));
    }

    @RequestMapping(
        value = "/create",
        method = RequestMethod.POST,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> create(@RequestBody Map<String, Object> payload) throws Exception{
        System.out.println((String)payload.get("username"));
        System.out.println((String)payload.get("password"));
        System.out.println((String)payload.get("name"));
        System.out.println((String)payload.get("dateOfBirth"));
        if(accountService.registerAccount(
            (String)payload.get("username"), 
            (String)payload.get("password"),
            (String)payload.get("name"),
            (String)payload.get("dateOfBirth")
        )){
            return ResponseEntity.ok().body("Success");
        }
        return ResponseEntity.badRequest().body("Failure");
    }

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
