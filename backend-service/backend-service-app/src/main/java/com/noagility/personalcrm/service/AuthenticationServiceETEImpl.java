package com.noagility.personalcrm.service;

import com.noagility.personalcrm.Util.JwtTokenUtil;
import com.noagility.personalcrm.model.Account;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
@Profile("ete")
public class AuthenticationServiceETEImpl extends AuthenticationService {
    @Override
    public ResponseEntity<?> authenticate(String username, String password, HttpServletResponse response) throws Exception {
        authenticate(username, password);

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(username);
        final String token = jwtTokenUtil.generateToken(userDetails);

        Account account = jwtTokenUtil.getAccountFromToken(token);

        String cookie = String.format("jwt=%s; Max-Age=%s; Path=%s; SameSite=%s; Domain=%s; Secure; " +
                        "accountID=%s; Max-Age=%s; Path=%s; SameSite=%s; Domain=%s; Secure",
                token,
                JwtTokenUtil.JWT_TOKEN_VALIDITY,
                "/",
                "None",
                getDomain(),
                account.getAccountID(),
                JwtTokenUtil.JWT_TOKEN_VALIDITY,
                "/",
                "None",
                getDomain()
        );
        return ResponseEntity.ok(cookie);
    }
}
