package com.noagility.personalcrm.controller;


import com.noagility.personalcrm.model.Account;
import com.noagility.personalcrm.model.TestClass;
import com.noagility.personalcrm.service.AccountService;
import com.noagility.personalcrm.service.CreateAccountService;
import com.noagility.personalcrm.service.DeleteAccountService;
import com.noagility.personalcrm.service.TestClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.Delete;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Account")

public class AccountController {
    @Autowired
    private CreateAccountService CreateAccountService;
    @Autowired
    private AccountService AccountService;

    @GetMapping("/dburl")
    public ResponseEntity<String> getDbPort() {
        return ResponseEntity.ok().body(System.getenv("SPRING_DATASOURCE_URL"));
    }

    @GetMapping("/create")
    public ResponseEntity<String> createAccount(@RequestParam String username, @RequestParam String password, @RequestParam String name, @RequestParam String dateOfBirth) {
        if(CreateAccountService.registerAccount(username, password, name, dateOfBirth)){
            return ResponseEntity.ok().body("Success");
        }
        return ResponseEntity.badRequest().body("Failure");
    }

    @GetMapping("/delete")
    public ResponseEntity<Account> deleteAccount() {
        Account account = DeleteAccountService.get();
        return ResponseEntity.ok().body(account);
    }

    @GetMapping("/getByUsername")
    public ResponseEntity<Account> getAccount(@RequestParam String username){
        return ResponseEntity.ok().body(AccountService.getByUsername(username));
    }
}
