package com.noagility.personalcrm.mapper;

import com.noagility.personalcrm.model.Chat;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class ChatRowMapper implements RowMapper<Chat>{
    @Override
    public Chat mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Chat(
            resultSet.getInt("ChatID"),
            asLocalDateTime(resultSet.getTimestamp("ChatCreation"))
        );
    }

    public static LocalDateTime asLocalDateTime(Timestamp date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
