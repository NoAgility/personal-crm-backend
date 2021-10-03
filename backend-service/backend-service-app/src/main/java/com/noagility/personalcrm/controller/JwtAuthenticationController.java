package com.noagility.personalcrm.controller;


import com.noagility.personalcrm.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping(value = "/authenticate")
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(
            value = "/login",
            method = RequestMethod.POST
    )
    @ResponseBody
    public ResponseEntity<?> createAuthenticationToken(@RequestBody Map<String, Object> payload, HttpServletResponse response) throws Exception {
        return authenticationService.authenticate((String)payload.get("username"), (String)payload.get("password"), response);
    }
}