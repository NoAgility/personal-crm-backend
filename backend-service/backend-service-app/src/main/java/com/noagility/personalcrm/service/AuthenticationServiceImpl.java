package com.noagility.personalcrm.service;

import com.noagility.personalcrm.Util.JwtTokenUtil;
import com.noagility.personalcrm.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@Profile("!ete")
public class AuthenticationServiceImpl extends AuthenticationService {
    @Override
    public ResponseEntity<?> authenticate(String username, String password, HttpServletResponse response) throws Exception {


        /**
         * Needs to be refactored out - Make a default builder method for cookies
         */
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


        return ResponseEntity.ok("Success");
    }
}
