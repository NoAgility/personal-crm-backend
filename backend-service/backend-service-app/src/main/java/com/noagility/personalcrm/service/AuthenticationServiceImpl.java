package com.noagility.personalcrm.service;

import com.noagility.personalcrm.Util.JwtTokenUtil;
import com.noagility.personalcrm.model.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;


@Slf4j
@Service
@Profile("!ete")
public class AuthenticationServiceImpl extends AuthenticationService {

    /**
     * Method to create a response entity containing Set-Cookie headers for a JWT token and accountID which can be used
     * to send authenticated requests to the backend
     * @param username The username of the account to authenticate
     * @param password The password of the account to authenticate
     * @param response The response that will be sent back to the client
     * @return A ResponseEntity object
     * @throws Exception Indicates that there was an issue validating the user details and creating a token
     */
    @Override
    public ResponseEntity<?> authenticate(String username, String password, HttpServletResponse response) throws Exception {

        authenticate(username, password);

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(username);
        final String token = jwtTokenUtil.generateToken(userDetails);

        Account account = jwtTokenUtil.getAccountFromToken(token);
        //  JWT Cookie
        Cookie jwtCookie = new Cookie("jwt", token);
        jwtCookie.setHttpOnly(false);
        jwtCookie.setMaxAge((int)JwtTokenUtil.JWT_TOKEN_VALIDITY);
        jwtCookie.setDomain(getDomain());
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");

        response.addCookie(jwtCookie);

        //  AccountID Cookie
        Cookie accountIdCookie = new Cookie("accountID", String.valueOf(account.getAccountID()));

        accountIdCookie.setHttpOnly(false);
        accountIdCookie.setMaxAge((int)JwtTokenUtil.JWT_TOKEN_VALIDITY);
        accountIdCookie.setDomain(getDomain());
        accountIdCookie.setSecure(true);
        accountIdCookie.setPath("/");

        response.addCookie(accountIdCookie);

        log.info("Received authentication request from username {}, sending JWT set-cookie header response", username);
        return ResponseEntity.ok("Success");
    }
}
