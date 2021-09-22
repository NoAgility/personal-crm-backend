package com.noagility.personalcrm.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class Account {
    private int accountID;
    private String accountUsername;
    private String accountName;
    private LocalDate accountDOB;
    private LocalDate accountCreation;
    private boolean accountActive = true;

    private static String DOB_DATE_FORMAT = "yyyy-MM-dd";

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

    public Account(int accountID, String accountUsername, String accountName, LocalDate accountDOB) {
        this.accountID = accountID;
        this.accountUsername = accountUsername;
        this.accountName = accountName;
        this.accountDOB = accountDOB;
    }

    public Account() {

    }

    @java.lang.Override
    public java.lang.String toString() {
        return "{" +
                "\"accountID\":" + accountID +
                ",\"accountUsername\":\"" + accountUsername + "\"" +
                ",\"accountName\":\"" + accountName + "\"" +
                ",\"accountDOB\":\"" + accountDOB + "\"" +
                ",\"accountCreation\":\"" + accountCreation + "\"" +
                ",\"accountActive\":" + accountActive +
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public LocalDate getAccountDOB() {
        return accountDOB;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
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

    @Override
    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }
        if(obj == null){
            return false;
        }
        if(getClass() != obj.getClass()){
            return false;
        }

        Account account = (Account) obj;
        if(
                accountID == account.getAccountID()
                        && accountUsername.equals(account.getAccountUsername())
                        && accountName.equals(account.getAccountName())
                        && accountDOB.equals(account.getAccountDOB())
                        && accountCreation.equals(account.getAccountCreation())
                        && accountActive == account.isAccountActive()
        ){
            return true;
        }
        return false;
    }
}