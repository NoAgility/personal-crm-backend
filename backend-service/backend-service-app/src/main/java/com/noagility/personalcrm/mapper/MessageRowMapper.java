package com.noagility.personalcrm.mapper;


import com.noagility.personalcrm.model.Message;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class MessageRowMapper implements RowMapper<Message>{
    @Override
    public Message mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Message(
            resultSet.getInt("MessageID"),
            resultSet.getInt("ChatID"),
            resultSet.getInt("AccountID"),
            asLocalDateTime(resultSet.getTimestamp("MessageTime")),
            resultSet.getString("MessageText")
        );
    }
    
    public static LocalDateTime asLocalDateTime(Timestamp date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
