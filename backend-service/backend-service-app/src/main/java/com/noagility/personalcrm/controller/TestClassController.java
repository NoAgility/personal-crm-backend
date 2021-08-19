package com.noagility.personalcrm.controller;


import com.noagility.personalcrm.model.TestClass;
import com.noagility.personalcrm.service.TestClassService;
import org.springframework.beans.factory.annotation.Autowired;
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
}
