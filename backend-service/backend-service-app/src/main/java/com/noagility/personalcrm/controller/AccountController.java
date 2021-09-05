package com.noagility.personalcrm.controller;


import java.util.Map;

import com.noagility.personalcrm.model.Account;
import com.noagility.personalcrm.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
