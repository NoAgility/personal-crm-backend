package com.noagility.personalcrm.service;

import com.noagility.personalcrm.Util.JwtTokenUtil;
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

        Cookie cookie = new Cookie("jwt", token);
        cookie.setMaxAge((int) JwtTokenUtil.JWT_TOKEN_VALIDITY);
        cookie.setHttpOnly(false);
        return ResponseEntity.ok(cookie);
    }
}
