package com.noagility.personalcrm.service;

import com.noagility.personalcrm.mapper.AccountRowMapper;
import com.noagility.personalcrm.model.Account;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountService {
    @Autowired
    DataSource dataSource;

    @Autowired
    AccountRowMapper accountRowMapper;


    public Account getByUsername(String username) {
        try {
            String sql = "SELECT * FROM Accounts WHERE AccountUsername = ?;";

            PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(sql);

            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            Account result = accountRowMapper.mapRow(rs, 0);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean registerAccount(String username, String password, String name, String dateOfBirth){
        //if(checkAccount(username)){
        // return false;
        //}
        //System.out.println("Reached here");
        try{
            //  Insert new account into Accounts table
            String sql = "INSERT INTO Accounts(AccountUsername, AccountName, AccountDOB) VALUES (?, ?, ?);";
            PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, dateOfBirth);
            preparedStatement.executeUpdate();

            //  Retrieve new account from the accounts table
            sql = "SELECT * FROM Accounts WHERE AccountUsername = ?;";
            preparedStatement = dataSource.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, username);
            Account account = accountRowMapper.mapRow(preparedStatement.executeQuery(), 0);

            //  Insert new account login details into AccountLoginDetials
            sql = "INSERT INTO AccountLoginDetails(AccountID, AccountUsername, AccountPassword) VALUES (?, ?, ?)";
            preparedStatement = dataSource.getConnection().prepareStatement(sql);
            preparedStatement.setInt(1, account.getAccountID());
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, password);
            preparedStatement.executeUpdate();
            return true;
        }
        catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    public boolean deactivateAccount(String username) {
        try{
            //  Insert new account into Accounts table
            String sql = "UPDATE Accounts SET AccountActive = 0 WHERE AccountUsername = ?;";
            PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.executeUpdate();
            //should add password deletion for security reasons
            return true;
        }
        catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }
}
