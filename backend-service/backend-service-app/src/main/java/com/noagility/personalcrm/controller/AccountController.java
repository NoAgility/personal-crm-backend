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
    private AccountService AccountService;

    @GetMapping("/dburl")
    public ResponseEntity<String> getDbPort() {
        return ResponseEntity.ok().body(System.getenv("SPRING_DATASOURCE_URL"));
    }
    @GetMapping(path ="/{account}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Account> getAccount(@PathVariable("account") String username) {
        return ResponseEntity.ok().body(AccountService.selectByUsername(username));
    }

    @GetMapping("/create")
    public ResponseEntity<Account> createAccount() {
        Account account = CreateAccountService.get();
        return ResponseEntity.ok().body(account);
    }

    @GetMapping("/delete")
    public ResponseEntity<Account> deleteAccount() {
        Account account = DeleteAccountService.get();
        return ResponseEntity.ok().body(account);
    }
}
