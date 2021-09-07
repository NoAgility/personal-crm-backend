package com.noagility.personalcrm.mapper;


import com.noagility.personalcrm.model.JwtRequest;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class LoginRowMapper implements RowMapper<JwtRequest> {
    @Override
    public JwtRequest mapRow(ResultSet resultSet, int i) throws SQLException {
        JwtRequest res = new JwtRequest();
        res.setUsername(resultSet.getString("AccountUsername"));
        res.setPassword(resultSet.getString("AccountPassword"));
        return res;
    }

}
