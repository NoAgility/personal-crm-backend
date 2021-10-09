package com.noagility.personalcrm.model;

public class TaskNote {
    private int taskID;
    private int taskNoteID;
    private String note;

    public TaskNote(int taskID, int taskNoteID, String note){
        this.taskID = taskID;
        this.taskNoteID = taskNoteID;
        this.note = note;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public int getTaskNoteID() {
        return taskNoteID;
    }

    public void setTaskNoteID(int taskNoteID) {
        this.taskNoteID = taskNoteID;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}