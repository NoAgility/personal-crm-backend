package com.noagility.personalcrm.mapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import com.noagility.personalcrm.model.Chat;

import org.springframework.jdbc.core.RowMapper;

public class ChatRowMapper implements RowMapper<Chat>{
    @Override
    public Chat mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Chat(
            resultSet.getInt("ChatID"),
            asLocalDate(resultSet.getDate("ChatCreation"))
        );
    }

    public static LocalDate asLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
