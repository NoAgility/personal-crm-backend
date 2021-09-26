package com.noagility.personalcrm.model;

public class TaskNote {
    private int taskID;
    private String taskNoteID;

    public TaskNote(int taskID, String taskNoteID){
        this.taskID = taskID;
        this.taskNoteID = taskNoteID;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public String getTaskNoteID() {
        return taskNoteID;
    }

    public void setTaskNoteID(String taskNoteID) {
        this.taskNoteID = taskNoteID;
    }
}