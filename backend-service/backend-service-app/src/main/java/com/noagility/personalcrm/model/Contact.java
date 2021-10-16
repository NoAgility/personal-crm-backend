package com.noagility.personalcrm.model;

import lombok.Data;

import java.time.LocalDate;
// Id of user added as contact and date contact added

@Data
public class Contact {

    private int contactID;
    private LocalDate contactCreatedOn;
    private String contactEmail;
    private String contactAddress;
    private String contactPhone;
    private String contactRole;

    public Contact(int contactID, LocalDate contactCreatedOn) {
        this.contactID = contactID;
        this.contactCreatedOn = contactCreatedOn;
    }

    public Contact() {

    }

    @Override
    public String toString() {
        return "Contact{" +
                "username='" + contactID + '\'' +
                ", contactCreatedOn=" + contactCreatedOn +
                '}';
    }
}
