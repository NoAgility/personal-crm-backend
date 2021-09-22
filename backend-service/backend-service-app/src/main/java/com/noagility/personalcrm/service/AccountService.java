package com.noagility.personalcrm.service;

import com.noagility.personalcrm.mapper.AccountRowMapper;
import com.noagility.personalcrm.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;


public class AccountService {
    @Autowired
    DataSource dataSource;

    @Autowired
    AccountRowMapper accountRowMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JdbcTemplate jdbcTemplate;
    private Object DataIntegrityViolationException;


    public Account getByUsername(String username) {
        try {
            String sql = "SELECT * FROM Accounts WHERE AccountUsername = ?";
            Account account = jdbcTemplate.queryForObject(sql, accountRowMapper, username);
            return account;
        }
        catch(Exception e){
            
        }
        return null;
    }

    public Account getByID(int id){
        try {
            String sql = "SELECT * FROM Accounts WHERE AccountID = ?";
            Account account = jdbcTemplate.queryForObject(sql, accountRowMapper, id);
            return account;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean registerAccount(String username, String password, String name, String dateOfBirth){
        if(getByUsername(username)!=null){
            System.out.println("username taken");
            //this should thow the error for repeated usename below
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
            return true;

        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            //Log failure to create new account due to existing username

        } catch (IncorrectResultSizeDataAccessException e) {
            e.printStackTrace();
            //Log failure to retrieve account after insertion
        }
        return false;
    }
    
    public boolean deactivateAccount(int id) {
        //  Insert new account into Accounts table
        String sql = "UPDATE Accounts SET AccountActive = 0 WHERE AccountID = ?;";
        return jdbcTemplate.update(sql, id) != 0;
    }
}
