package com.noagility.personalcrm.service;

import com.noagility.personalcrm.Util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.servlet.http.HttpServletResponse;

public abstract class AuthenticationService {

    @Autowired
    protected AuthenticationManager authenticationManager;

    @Autowired
    protected JwtTokenUtil jwtTokenUtil;

    @Autowired
    protected JwtUserDetailsService userDetailsService;

    @Value("${domain:localhost}")
    private String domain;

    public abstract ResponseEntity<?> authenticate(String usernmame, String password, HttpServletResponse response) throws Exception;

    /**
     * Method to authenticate a user
     * @param username The username passed
     * @param password The password passed
     * @throws Exception Indicates that there was an issue authenticating the user details
     */
    protected void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
    protected String getDomain() {
        return domain;
    }
}
