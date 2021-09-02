package com.noagility.personalcrm.service;

import com.noagility.personalcrm.mapper.AccountRowMapper;
import com.noagility.personalcrm.mapper.TestClassRowMapper;
import com.noagility.personalcrm.model.Account;
import com.noagility.personalcrm.model.TestClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccountService {
    @Autowired
    DataSource dataSource;
    @Autowired
    AccountRowMapper AccountRowMapper;
    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Account> getSample() {

        return Arrays.asList(new Account[] {new Account(1, 1, "orock", LocalDate.of(2000, Month.APRIL, 4), LocalDate.of(2021, Month.SEPTEMBER, 2)),
                new Account(2, 1, "amula", LocalDate.of(1999, Month.SEPTEMBER, 13), LocalDate.of(2021, Month.AUGUST, 31)),
                new Account(3, 1, "nrick", LocalDate.of(2002, Month.NOVEMBER, 27), LocalDate.of(2021, Month.SEPTEMBER, 1))});
    }
    public Account selectByUsername(String query) {
        try {
            String sql = "SELECT * FROM test WHERE test.username=?";

            PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(sql);

            preparedStatement.setString(1, query);

            ResultSet rs = preparedStatement.executeQuery();

            Account result = AccountRowMapper.mapRow(rs, 0);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
