package com.noagility.personalcrm.service;

import com.noagility.personalcrm.Util.JwtTokenUtil;
import com.noagility.personalcrm.mapper.AccountRowMapper;
import com.noagility.personalcrm.model.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Slf4j
public class AccountService {
    @Autowired
    DataSource dataSource;

    @Autowired
    AccountRowMapper accountRowMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    public Account getByUsername(String username) {
        try {
            String sql = "SELECT * FROM Accounts WHERE AccountUsername = ?";
            Account account = jdbcTemplate.queryForObject(sql, accountRowMapper, username);
            return account;
        }
        catch(Exception e){
            log.error("Tried to fetch account by username {}, does not exist", username);
        }
        return null;
    }

    public Account getByID(int id){
        try {
            String sql = "SELECT * FROM Accounts WHERE AccountID = ?";
            Account account = jdbcTemplate.queryForObject(sql, accountRowMapper, id);
            return account;
        } catch (Exception e) {
            log.error("Failed to fetch account by id {}", id, e);
        }
        return null;
    }

    public boolean registerAccount(String username, String password, String name, String dateOfBirth){
        if(getByUsername(username)!=null){
            log.info(String.format("Tried to register account with username that already exists, Username=%s", username));
            //this should throw the error for repeated username below
            return false;
        }
        try {
            //  Insert new account into Accounts table

            String sql = "INSERT INTO Accounts(AccountUsername, AccountName, AccountDOB) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, username, name, dateOfBirth);

            //  Retrieve new account from the accounts table
            sql = "SELECT * FROM Accounts WHERE AccountUsername = ?";
            Account account = jdbcTemplate.queryForObject(sql, accountRowMapper, username);

            //  Insert new account login details into AccountLoginDetials
            sql = "INSERT INTO AccountLoginDetails(AccountID, AccountUsername, AccountPassword) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, account.getAccountID(), username, passwordEncoder.encode(password));

            log.info("Account has been registered with details: Username={}, Name={}, DateOfBirth={}", username, name, dateOfBirth);

            return true;

        } catch (DataIntegrityViolationException e) {
            log.error("Failed to create new account with username={} because it already exists", username);
            //Log failure to create new account due to existing username

        } catch (IncorrectResultSizeDataAccessException e) {
            log.error("Failed to retrieve account with username={} after inserting account into DB", username);
            //Log failure to retrieve account after insertion
        }
        return false;
    }

    public boolean deactivateAccount(int id) {
        //  Insert new account into Accounts table
        String sql = "UPDATE Accounts SET AccountActive = 0 WHERE AccountID = ?;";

        log.info("Account (id: {}) has been deactivated", id);

        return jdbcTemplate.update(sql, id) != 0;
    }


}
