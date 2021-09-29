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

@Service
@Profile("!ete")
public class AuthenticationServiceImpl extends AuthenticationService {
    @Override
    public ResponseEntity<?> authenticate(String username, String password, HttpServletResponse response) throws Exception {

        authenticate(username, password);

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(username);
        final String token = jwtTokenUtil.generateToken(userDetails);

        //  JWT Cookie
        Cookie cookie = new Cookie("jwt", token);
        cookie.setMaxAge((int)JwtTokenUtil.JWT_TOKEN_VALIDITY);
        cookie.setHttpOnly(false);
        cookie.setPath("/");
        response.addCookie(cookie);

        //  Set the userID as a cookie
        Account account = jwtTokenUtil.getAccountFromToken(token);
        cookie = new Cookie("accountID", Integer.toString(account.getAccountID()));
        cookie.setMaxAge((int)JwtTokenUtil.JWT_TOKEN_VALIDITY);
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.ok("Success");
    }
}
