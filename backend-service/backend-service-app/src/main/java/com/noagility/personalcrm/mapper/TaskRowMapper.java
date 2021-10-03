package com.noagility.personalcrm.mapper;

import com.noagility.personalcrm.model.Task;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TaskRowMapper implements RowMapper<Task>{

    @Override
    public Task mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Task(
                resultSet.getInt("TaskID"),
                resultSet.getInt("AccountID"),
                resultSet.getString("TaskName"),
                asLocalDateTime(resultSet.getTimestamp("TaskDeadline")),
                resultSet.getInt("TaskPriority")
        );
    }

    public static LocalDateTime asLocalDateTime(Timestamp date) {
        if(date != null){
            return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
        return null;

    }
}