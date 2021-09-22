package com.noagility.personalcrm.mapper;

import com.noagility.personalcrm.model.TaskNote;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskNoteRowMapper implements RowMapper<TaskNote> {
    @Override
    public TaskNote mapRow(ResultSet resultSet, int i) throws SQLException {
        return new TaskNote(
                resultSet.getInt("TaskID"),
                resultSet.getString("TaskNoteID")
        );
    }
}
