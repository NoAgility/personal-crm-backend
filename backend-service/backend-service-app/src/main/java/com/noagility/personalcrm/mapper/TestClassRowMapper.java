package com.noagility.personalcrm.mapper;

import com.noagility.personalcrm.model.TestClass;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TestClassRowMapper implements RowMapper<TestClass> {

    @Override
    public TestClass mapRow(ResultSet resultSet, int i) throws SQLException {
        TestClass res = new TestClass();

        resultSet.next();
        res.setId(resultSet.getString("id"));
        res.setName(resultSet.getString("username"));
        res.setAddress("N/A");
        return res;
    }
}
