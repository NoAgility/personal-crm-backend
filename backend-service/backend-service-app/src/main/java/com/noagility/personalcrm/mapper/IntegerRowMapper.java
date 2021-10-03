package com.noagility.personalcrm.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class IntegerRowMapper implements RowMapper<Integer>{
    @Override
    public Integer mapRow(ResultSet res, int i) throws SQLException{
        return res.getInt("Integer");
    }
}
