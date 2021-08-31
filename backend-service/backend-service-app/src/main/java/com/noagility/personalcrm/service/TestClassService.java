package com.noagility.personalcrm.service;


import com.noagility.personalcrm.mapper.TestClassRowMapper;
import com.noagility.personalcrm.model.TestClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class TestClassService {
    @Autowired
    DataSource dataSource;
    @Autowired
    TestClassRowMapper testClassRowMapper;
    @Autowired
    JdbcTemplate jdbcTemplate;

    public TestClass selectByUsername(String query) {
        try {
            String sql = "SELECT * FROM test WHERE test.username=?";

            PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(sql);

            preparedStatement.setString(1, query);

            ResultSet rs = preparedStatement.executeQuery();

            TestClass result = testClassRowMapper.mapRow(rs, 0);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}