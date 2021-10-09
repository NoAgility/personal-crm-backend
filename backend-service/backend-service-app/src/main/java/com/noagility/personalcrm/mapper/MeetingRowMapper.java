package com.noagility.personalcrm.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.noagility.personalcrm.model.Meeting;

import org.springframework.jdbc.core.RowMapper;

public class MeetingRowMapper implements RowMapper<Meeting>{
    @Override
    public Meeting mapRow(ResultSet resultSet, int i) throws SQLException{
        return new Meeting(
            resultSet.getInt("MeetingID"),
            resultSet.getString("MeetingName"),
            resultSet.getString("MeetingDescription"),
            resultSet.getInt("meetingCreatorID"),
            asLocalDateTime(resultSet.getTimestamp("MeetingStart")),
            asLocalDateTime(resultSet.getTimestamp("MeetingEnd")),
            asLocalDateTime(resultSet.getTimestamp("MeetingCreation"))
        );
    }

    public static LocalDateTime asLocalDateTime(Timestamp date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
