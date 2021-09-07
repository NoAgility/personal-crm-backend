package com.noagility.personalcrm.service;

import com.noagility.personalcrm.mapper.LoginRowMapper;
import com.noagility.personalcrm.model.JwtRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    LoginRowMapper loginRowMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            String sql = "SELECT * FROM AccountLoginDetails WHERE AccountUsername = ?";
            JwtRequest jwtRequest = jdbcTemplate.queryForObject(sql, loginRowMapper, username);

            return new User(jwtRequest.getUsername(), jwtRequest.getPassword(), new ArrayList<>());
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
        }
        return null;

    }
}