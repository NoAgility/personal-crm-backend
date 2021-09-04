package com.noagility.personalcrm.controller;


import com.noagility.personalcrm.model.Account;
import com.noagility.personalcrm.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Account")

public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/dburl")
    public ResponseEntity<String> getDbPort() {
        return ResponseEntity.ok().body(System.getenv("SPRING_DATASOURCE_URL"));
    }

    @GetMapping("/create")
    public ResponseEntity<String> createAccount(@RequestParam String username, @RequestParam String password, @RequestParam String name, @RequestParam String dateOfBirth) {
        if(accountService.registerAccount(username, password, name, dateOfBirth)){
            return ResponseEntity.ok().body("Success");
        }
        return ResponseEntity.badRequest().body("Failure");
    }

    @GetMapping("/deactivate")
    public ResponseEntity<String> deactivateAccount(@RequestParam String username) {
        if(accountService.deactivateAccount(username)){
            return ResponseEntity.ok().body("Success");
        }
        return ResponseEntity.badRequest().body("Failure");
    }

    @GetMapping("/getByUsername")
    public ResponseEntity<Account> getAccount(@RequestParam String username){
        return ResponseEntity.ok().body(accountService.getByUsername(username));
    }
}
