package com.noagility.personalcrm.service;

import com.noagility.personalcrm.mapper.ContactRowMapper;
import com.noagility.personalcrm.model.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

public class ContactService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    ContactRowMapper contactRowMapper;

    @Autowired
    DataSource dataSource;

    public boolean addContact(int accountID, String contact) {

        int contactID = getIDFromUsername(contact);

        if(accountID==contactID){
            return false;
            //cannot add urself
        }
        if(contactAdded(accountID ,contactID)){
            System.out.println("contact already added");
            //this should thow  error if db already has contact
            return false;
        }
        try {
            //  Insert new contact into Contacts table

            String sql = "INSERT INTO Account_Contacts(AccountID, ContactID) VALUES (?, ?)";
            jdbcTemplate.update(sql, accountID, contactID);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addContactWithUsername(String username, String contact) {
        int accountID = getIDFromUsername(username);
        return addContact(accountID, contact);
    }


    public boolean updateContact(int accountID, int contactID, String email, String address, String phone, String jobTitle, String company) {

        try {
                jdbcTemplate.update("UPDATE Account_Contacts SET ContactEmail = ?, ContactAddress = ?," +
                        " ContactPhone = ?, ContactJobTitle = ?, ContactCompany = ? " +
                        "WHERE ContactID = ? AND AccountID = ?", email, address, phone, jobTitle, company, contactID, accountID);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteContact(int accountID, String contact) {

        int contactID = getIDFromUsername(contact);

        try {
            //  Insert new contact into Contacts table

            String sql = "DELETE FROM Account_Contacts WHERE AccountID = ? AND ContactID = ?";
            jdbcTemplate.update(sql, accountID, contactID);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Contact> getContacts(int accountID) {

        try {

            String sql = "SELECT ContactID, ContactCreatedOn, ContactEmail, ContactAddress, ContactPhone, ContactCompany, ContactJobTitle FROM Account_Contacts WHERE AccountID = ?";

            List<Contact> contacts = jdbcTemplate.query(sql, contactRowMapper, accountID);

            return contacts;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean contactAdded(int accountID, int contactID) {
        List<Contact> contacts = getContacts(accountID);
        for(int i=0; i<contacts.size(); i++){
            if(contacts.get(i).getContactID() == contactID){
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
