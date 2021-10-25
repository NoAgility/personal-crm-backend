package com.noagility.personalcrm.service;

import com.noagility.personalcrm.mapper.LoginRowMapper;
import com.noagility.personalcrm.model.JwtRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    LoginRowMapper loginRowMapper;

    /**
     * Method to load a username into a UserDetails object
     * @param username The username to load
     * @return a UserDetails object
     * @throws UsernameNotFoundException Indicates that the service failed to create a UserDetails object from the username
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {

            String sql = "SELECT AccountLoginDetails.AccountUsername, AccountLoginDetails.AccountPassword " +
                    "FROM AccountLoginDetails INNER JOIN Accounts ON " +
                    "AccountLoginDetails.AccountID = Accounts.AccountID WHERE AccountLoginDetails.AccountUsername = ? AND AccountActive = 1";
            try {
                JwtRequest jwtRequest = jdbcTemplate.queryForObject(sql, loginRowMapper, username);
                return new User(jwtRequest.getUsername(), jwtRequest.getPassword(), new ArrayList<>());
            } catch (Exception e) {
                log.error("Failed to fetch userDetails from username and password encoded in JWT token - username={}", username, e);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}