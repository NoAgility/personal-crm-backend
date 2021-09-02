package com.noagility.personalcrm.model;

import java.time.LocalDate;

public class Account {
    private int AccountID;
    private int RoleID;
    private String AccountName;
    private LocalDate AccountDOB;
    private LocalDate AccountCreation;

    public Account(int accountID, int roleID, String accountName, LocalDate accountDOB, LocalDate accountCreation) {
        AccountID = accountID;
        RoleID = roleID;
        AccountName = accountName;
        AccountDOB = accountDOB;
        AccountCreation = accountCreation;
    }

    public Account(int roleID, String accountName, LocalDate accountDOB, LocalDate accountCreation) {
        RoleID = roleID;
        AccountName = accountName;
        AccountDOB = accountDOB;
        AccountCreation = accountCreation;
    }

    public Account() {

    }

    @java.lang.Override
    public java.lang.String toString() {
        return "Account{" +
                "AccountID=" + AccountID +
                ", RoleID=" + RoleID +
                ", AccountName='" + AccountName + '\'' +
                ", AccountDOB=" + AccountDOB +
                ", AccountCreation=" + AccountCreation +
                '}';
    }

    public int getAccountID() {
        return AccountID;
    }

    public int getRoleID() {
        return RoleID;
    }

    public String getAccountName() {
        return AccountName;
    }

    public LocalDate getAccountDOB() {
        return AccountDOB;
    }

    public LocalDate getAccountCreation() {
        return AccountCreation;
    }

    public void setAccountID(int accountID) {
        AccountID = accountID;
    }

    public void setRoleID(int roleID) {
        RoleID = roleID;
    }

    public void setAccountName(String accountName) {
        AccountName = accountName;
    }

    public void setAccountDOB(LocalDate accountDOB) {
        AccountDOB = accountDOB;
    }

    public void setAccountCreation(LocalDate accountCreation) {
        AccountCreation = accountCreation;
    }
}
