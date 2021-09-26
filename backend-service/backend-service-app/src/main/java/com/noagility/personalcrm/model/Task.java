package com.noagility.personalcrm.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public class Task {
    private int TaskID;
    private int accountID;
    private String taskName;
    private LocalDateTime taskDeadline;
    private int taskPriority;
    private List<TaskNote> taskNoteList;
    private List<TaskContactAccount> taskContactAccounts;

    public Task(int taskID, int accountID, String taskName, LocalDateTime taskDeadline, int taskPriority){
        this.TaskID = taskID;
        this.accountID = accountID;
        this.taskName = taskName;
        this.taskDeadline = taskDeadline;
        this.taskPriority = taskPriority;
    }


    public int getTaskID() {
        return TaskID;
    }

    public void setTaskID(int taskID) {
        TaskID = taskID;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getTaskDeadline() {
        return taskDeadline;
    }

    public void setTaskDeadline(LocalDateTime taskDeadline) {
        this.taskDeadline = taskDeadline;
    }

    public int getTaskPriority() {
        return taskPriority;
    }

    public void setTaskPriority(int taskPriority) {
        this.taskPriority = taskPriority;
    }

    public List<TaskNote> getTaskNoteList() {
        return taskNoteList;
    }

    public void setTaskNoteList(List<TaskNote> taskNoteList) {
        this.taskNoteList = taskNoteList;
    }

    public List<TaskContactAccount> getTaskContactAccounts() {
        return taskContactAccounts;
    }

    public void setTaskContactAccounts(List<TaskContactAccount> taskContactAccounts) {
        this.taskContactAccounts = taskContactAccounts;
    }
}