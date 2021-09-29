package com.noagility.personalcrm.controller;

import com.noagility.personalcrm.Util.JwtTokenUtil;
import com.noagility.personalcrm.model.Account;
import com.noagility.personalcrm.model.JwtRequest;
import com.noagility.personalcrm.model.JwtResponse;
import com.noagility.personalcrm.service.AuthenticationService;
import com.noagility.personalcrm.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping(value = "/authenticate")
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> createAuthenticationToken(@RequestBody Map<String, Object> payload, HttpServletResponse response) throws Exception {
        return authenticationService.authenticate((String)payload.get("username"), (String)payload.get("password"), response);
    }
}