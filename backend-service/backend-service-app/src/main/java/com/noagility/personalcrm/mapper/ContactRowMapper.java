package com.noagility.personalcrm.mapper;

import com.noagility.personalcrm.model.Contact;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class ContactRowMapper implements RowMapper<Contact> {
    @Override
    public Contact mapRow(ResultSet resultSet, int i) throws SQLException {
        Contact res = new Contact();
        res.setContactID(resultSet.getInt("ContactID"));
        res.setContactCreatedOn(asLocalDate(resultSet.getDate("ContactCreatedOn")));
        System.out.println(res.toString());
        return res;
    }
    public static LocalDate asLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }
}