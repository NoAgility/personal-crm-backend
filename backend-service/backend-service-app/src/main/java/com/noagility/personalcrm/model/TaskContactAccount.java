package com.noagility.personalcrm.model;

public class TaskContactAccount {
    private int taskID;
    private int contactID;

    public TaskContactAccount(int taskID, int contactID) {
        this.taskID = taskID;
        this.contactID = contactID;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public int getContactID() {
        return contactID;
    }

    public void setContactID(int contactID) {
        this.contactID = contactID;
    }
}
