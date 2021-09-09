package com.noagility.personalcrm.controller;

import com.noagility.personalcrm.Util.JwtTokenUtil;
import com.noagility.personalcrm.model.JwtRequest;
import com.noagility.personalcrm.model.JwtResponse;
import com.noagility.personalcrm.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;


@RestController
@CrossOrigin
@RequestMapping(value = "/authenticate")
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody Map<String, Object> payload) throws Exception {

        authenticate((String)payload.get("username"), (String)payload.get("password"));

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername((String)payload.get("username"));
        final String token = jwtTokenUtil.generateToken(userDetails);
        Date expDate = jwtTokenUtil.getExpirationDateFromToken(token);
        return ResponseEntity.ok(new JwtResponse(token, expDate));

    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}