package com.noagility.personalcrm.service;

import com.noagility.personalcrm.mapper.ContactRowMapper;
import com.noagility.personalcrm.model.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class ContactService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    ContactRowMapper contactRowMapper;

    @Autowired
    DataSource dataSource;

    public boolean addContact(String username, String contact) {
        int usernameID = getIDFromUsername(username);
        int contactID = getIDFromUsername(contact);

        if(contactAdded(username,contactID)){
            System.out.println("contact already added");
            //this should thow  error if db already has contact
            return false;
        }
        try {
            //  Insert new contact into Contacts table

            String sql = "INSERT INTO Account_Contacts(AccountID, ContactID) VALUES (?, ?)";
            jdbcTemplate.update(sql, usernameID, contactID);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



    public boolean updateContact(String username) {
       return false;
    }

    public boolean deleteContact(String username, String contact) {

        int usernameID = getIDFromUsername(username);
        int contactID = getIDFromUsername(contact);

        try {
            //  Insert new contact into Contacts table

            String sql = "DELETE FROM Account_Contacts WHERE AccountID = ? AND ContactID = ?";
            jdbcTemplate.update(sql, usernameID, contactID);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Contact> getContacts(String username) {
        int usernameID = getIDFromUsername(username);

        try {

            String sql = "SELECT ContactID, ContactCreatedOn FROM Account_Contacts WHERE AccountID = ?";

            List<Contact> contacts = jdbcTemplate.query(sql, contactRowMapper, usernameID);

            return contacts;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean contactAdded(String username, int contactID) {
        List<Contact> contacts = getContacts(username);
        for(int i=0; i<contacts.size(); i++){
            if(contacts.get(i).getContactID() ==contactID){
                return true;
            }
        }
        return false;
    }

    public Integer getIDFromUsername(String username) {
        try {
            String sql = "SELECT AccountID FROM Accounts WHERE AccountUsername = ?";
            int id = jdbcTemplate.queryForObject(sql,Integer.class, username);
            return id;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}