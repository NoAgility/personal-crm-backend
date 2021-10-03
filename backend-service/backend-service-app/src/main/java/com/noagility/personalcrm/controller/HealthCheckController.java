package com.noagility.personalcrm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/healthcheck")
public class HealthCheckController {

    @RequestMapping(
            value="/update",
            method= RequestMethod.GET
    )
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok("Healthy");
    }
}
