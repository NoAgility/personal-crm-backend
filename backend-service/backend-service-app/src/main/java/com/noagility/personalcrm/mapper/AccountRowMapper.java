package com.noagility.personalcrm.mapper;

import com.noagility.personalcrm.model.Account;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class AccountRowMapper implements RowMapper<Account> {
    @Override
    public Account mapRow(ResultSet resultSet, int i) throws SQLException {
        Account res = new Account();
        res.setAccountID(resultSet.getInt("AccountID"));
        res.setAccountUsername(resultSet.getString("AccountUsername"));
        res.setAccountName(resultSet.getString("AccountName"));
        res.setAccountDOB(asLocalDate(resultSet.getDate("AccountDOB")));
        res.setAccountCreation(asLocalDate(resultSet.getDate("AccountCreation")));
        res.setAccountActive(resultSet.getBoolean("AccountActive"));
        System.out.println(res.toString());
        return res;
    }
    public static LocalDate asLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
