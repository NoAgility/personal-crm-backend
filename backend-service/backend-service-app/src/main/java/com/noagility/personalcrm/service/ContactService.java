package com.noagility.personalcrm.service;

import com.noagility.personalcrm.mapper.ContactRowMapper;
import com.noagility.personalcrm.model.Contact;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ContactService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    ContactRowMapper contactRowMapper;

    @Autowired
    DataSource dataSource;

    /**
     * Method to add a contact to an account
     * @param accountID The id of the account to add the contact
     * @param contact The id of the account to be added
     * @return a boolean indicating the success of the transaction
     */
    public boolean addContact(int accountID, String contact) {

        Integer contactID = getIDFromUsername(contact);

        if(accountID==contactID){
            log.info("Account (id: {}) attempted to add themselves", accountID);
            return false;
            //cannot add urself
        }
        if(contactAdded(accountID ,contactID)){
            log.info("Attempted to add contact (id: {}) by account (id: {}), however already has been added", contactID, accountID);
            //this should thow  error if db already has contact
            return false;
        }
        try {
            //  Insert new contact into Contacts table

            String sql = "INSERT INTO Account_Contacts(AccountID, ContactID) VALUES (?, ?)";
            jdbcTemplate.update(sql, accountID, contactID);
            log.info("Contact (id: {}) added by account (id: {})", contactID, accountID);
            return true;

        } catch (Exception e) {
            log.error("Failed to add contact (id: {}) for account (id: {})", contactID, accountID);
        }
        return false;
    }

    /**
     * Method to add a contact to an account through it's username
     * @param username The username of the account to add the contact
     * @param contact The id of the account to be added
     * @return a boolean indicating the success of the transaction
     */
    public boolean addContactWithUsername(String username, String contact) {
        int accountID = getIDFromUsername(username);
        return addContact(accountID, contact);
    }

    /**
     * Method to update the personal information of a particular contact
     * @param accountID The id of the account that stores the contact
     * @param contactID The id of the account stored as a contact
     * @param email The custom email value stored for the relationship
     * @param address The custom address value stored for the relationship
     * @param phone The custom phone value stored for the relationship
     * @param jobTitle The custom jobTitle value stored for the relationship
     * @param company The custom company value stored for the relationship
     * @return a boolean indicating the success of the transaction
     */
    public boolean updateContact(int accountID, int contactID, String email, String address, String phone, String jobTitle, String company) {

        try {
                jdbcTemplate.update("UPDATE Account_Contacts SET ContactEmail = ?, ContactAddress = ?," +
                        " ContactPhone = ?, ContactJobTitle = ?, ContactCompany = ? " +
                        "WHERE ContactID = ? AND AccountID = ?", email, address, phone, jobTitle, company, contactID, accountID);
            log.info("Contact (id: {}) updated by account (id: {}), new details: email={}, address={}, phone={}, jobTitle={}, company={}", contactID, accountID, email, address, phone, jobTitle, company);
            return true;
        } catch (Exception e) {
            log.error("Failed to update contact (id: {}) for account (id: {})", contactID, accountID, e);
        }
        return false;
    }

    /**
     * Method to delete a contact
     * @param accountID The id of the account that owns the contact
     * @param contact The id of the account to be deleted from contacts
     * @return a boolean indicating the success of the transaction
     */
    public boolean deleteContact(int accountID, String contact) {

        int contactID = getIDFromUsername(contact);

        try {
            //  Insert new contact into Contacts table

            String sql = "DELETE FROM Account_Contacts WHERE AccountID = ? AND ContactID = ?";
            jdbcTemplate.update(sql, accountID, contactID);
            log.info("Contact (id: {}) deleted by account (id: {})", contactID, accountID);
            return true;

        } catch (Exception e) {
            log.error("Failed to delete contact (id: {}) for account (id: {})", contactID, accountID, e);
        }
        return false;
    }

    /**
     * Method to get all the contacts of an account
     * @param accountID The id of the account to fetch contacts for
     * @return A List of contact objects
     */
    public List<Contact> getContacts(int accountID) {

        try {

            String sql = "SELECT ContactID, ContactCreatedOn, ContactEmail, ContactAddress, ContactPhone, ContactCompany, ContactJobTitle FROM Account_Contacts WHERE AccountID = ?";

            List<Contact> contacts = jdbcTemplate.query(sql, contactRowMapper, accountID);

            return contacts;

        } catch (Exception e) {
            log.error("Failed to fetch contacts for account (id: {})", accountID, e);
        }
        return null;
    }

    /**
     * Method to validate if an account has a contact added
     * @param accountID The id of the account that should contain the contact
     * @param contactID The id of the account that should be the contact
     * @return a boolean indicating the success of the transaction
     */
    private boolean contactAdded(int accountID, int contactID) {
        List<Contact> contacts = getContacts(accountID);
        for(int i=0; i<contacts.size(); i++){
            if(contacts.get(i).getContactID() == contactID){
                return true;
            }
        }
        return false;
    }

    /**
     * Method to get the accountID of an account by username
     * @param username The username of the account
     * @return An Integer representing the accountID
     */
    public Integer getIDFromUsername(String username) {
        try {
            String sql = "SELECT AccountID FROM Accounts WHERE AccountUsername = ?";
            int id = jdbcTemplate.queryForObject(sql,Integer.class, username);
            return id;
        } catch (EmptyResultDataAccessException e) {
            log.error("Failed to get id from username {}", username, e);
        }
        return null;
    }
}
