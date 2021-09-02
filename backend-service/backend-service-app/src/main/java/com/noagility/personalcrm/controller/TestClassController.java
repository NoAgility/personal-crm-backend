package com.noagility.personalcrm.controller;


import com.noagility.personalcrm.model.TestClass;
import com.noagility.personalcrm.service.TestClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/testclass")
public class TestClassController {

    @Autowired
    private TestClassService testClassService;

    @GetMapping("/test")
    public ResponseEntity<List<TestClass>> getAllStudents() {
        List<TestClass> list = testClassService.get();
        return ResponseEntity.ok().body(list);
    }
    @GetMapping("/dburl")
    public ResponseEntity<String> getDbPort() {
        return ResponseEntity.ok().body(System.getenv("SPRING_DATASOURCE_URL"));
    }
    @GetMapping(path ="/user/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TestClass> getUser(@PathVariable("username") String username) {
        return ResponseEntity.ok().body(testClassService.selectByUsername(username));
    }
}
