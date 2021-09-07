package com.noagility.personalcrm.model;

import java.time.LocalDate;

public class Account {
    private int accountID;
    private String accountUsername;
    private String accountName;
    private LocalDate accountDOB;
    private LocalDate accountCreation;
    private boolean accountActive;

    public Account(int accountID, String accountUsername, String accountName, LocalDate accountDOB, LocalDate accountCreation) {
        this.accountID = accountID;
        this.accountUsername = accountUsername;
        this.accountName = accountName;
        this.accountDOB = accountDOB;
        this.accountCreation = accountCreation;
    }

    public Account(String accountUsername, String accountName, LocalDate accountDOB, LocalDate accountCreation) {
        this.accountUsername = accountUsername;
        this.accountName = accountName;
        this.accountDOB = accountDOB;
        this.accountCreation = accountCreation;
    }

    public Account() {

    }

    @java.lang.Override
    public java.lang.String toString() {
        return "Account{" +
                "accountID=" + accountID +
                ", accountUsername=" + accountUsername +
                ", accountName='" + accountName + '\'' +
                ", accountDOB=" + accountDOB +
                ", accountCreation=" + accountCreation +
                '}';
    }

    public void setAccountActive(boolean accountActive) {
        this.accountActive = accountActive;
    }

    public boolean isAccountActive() {
        return accountActive;
    }

    public int getAccountID() {
        return accountID;
    }

    public String getAccountUsername(){
        return accountUsername;
    }

    public String getAccountName() {
        return accountName;
    }

    public LocalDate getAccountDOB() {
        return accountDOB;
    }

    public LocalDate getAccountCreation() {
        return accountCreation;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public void setAccountUsername(String accountUsername) {
        this.accountUsername = accountUsername;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setAccountDOB(LocalDate accountDOB) {
        this.accountDOB = accountDOB;
    }

    public void setAccountCreation(LocalDate accountCreation) {
        this.accountCreation = accountCreation;
    }
}