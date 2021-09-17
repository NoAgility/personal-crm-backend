package com.noagility.personalcrm.mapper;


import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import com.noagility.personalcrm.model.Message;

import org.springframework.jdbc.core.RowMapper;

public class MessageRowMapper implements RowMapper<Message>{
    @Override
    public Message mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Message(
            resultSet.getInt("MessageID"),
            resultSet.getInt("ChatID"),
            resultSet.getInt("AccountID"),
            asLocalDate(resultSet.getDate("MessageTime")),
            resultSet.getString("MessageText")
        );
    }
    
    public static LocalDate asLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
