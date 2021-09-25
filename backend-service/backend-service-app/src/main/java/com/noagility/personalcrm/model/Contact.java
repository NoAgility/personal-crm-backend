package com.noagility.personalcrm.model;

import java.time.LocalDate;
// Id of user added as contact and date contact added
public class Contact {

    private int contactID;
    private LocalDate contactCreatedOn;

    public Contact(int contactID, LocalDate contactCreatedOn) {
        this.contactID = contactID;
        this.contactCreatedOn = contactCreatedOn;
    }

    public Contact() {

    }

    public Contact(int i) {
        this.contactID=i;
    }

    public int getContactID() {
        return contactID;
    }

    public LocalDate getContactCreatedOn() {
        return contactCreatedOn;
    }

    public void setContactID(int contactID) {
        this.contactID = contactID;
    }

    public void setContactCreatedOn(LocalDate contactCreatedOn) {
        this.contactCreatedOn = contactCreatedOn;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "username='" + contactID + '\'' +
                ", contactCreatedOn=" + contactCreatedOn +
                '}';
    }
}
