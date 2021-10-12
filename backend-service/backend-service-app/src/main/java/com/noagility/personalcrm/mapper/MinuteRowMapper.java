package com.noagility.personalcrm.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.noagility.personalcrm.model.Minute;

import org.springframework.jdbc.core.RowMapper;

public class MinuteRowMapper implements RowMapper<Minute>{
    @Override
    public Minute mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Minute(
            resultSet.getInt("MinuteID"),
            resultSet.getInt("MeetingID"),
            resultSet.getInt("AccountID"),
            resultSet.getString("MinuteText"),
            asLocalDateTime(resultSet.getTimestamp("MinuteCreation"))
        );
    }
    
    public static LocalDateTime asLocalDateTime(Timestamp date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
