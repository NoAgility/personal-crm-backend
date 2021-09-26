package com.noagility.personalcrm.mapper;

import com.noagility.personalcrm.model.TaskContactAccount;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskContactRowMapper implements RowMapper<TaskContactAccount> {
    @Override
    public TaskContactAccount mapRow(ResultSet resultSet, int i) throws SQLException {
        return new TaskContactAccount(
                resultSet.getInt("TaskID"),
                resultSet.getInt("ContactID")
        );
    }
}