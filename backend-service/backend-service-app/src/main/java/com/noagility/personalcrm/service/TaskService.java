package com.noagility.personalcrm.service;

import com.noagility.personalcrm.mapper.TaskContactMapper;
import com.noagility.personalcrm.mapper.TaskNoteRowMapper;
import com.noagility.personalcrm.mapper.TaskRowMapper;
import com.noagility.personalcrm.model.Task;
import com.noagility.personalcrm.model.TaskContactAccount;
import com.noagility.personalcrm.model.TaskNote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TaskService {
    @Autowired
    DataSource dataSource;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    TaskRowMapper taskRowMapper;

    @Autowired
    TaskNoteRowMapper taskNoteRowMapper;

    @Autowired
    TaskContactMapper taskContactMapper;

    private int maxTaskID;

    @EventListener(ApplicationReadyEvent.class)
    private void loadTaskID(){
        String sql = "SELECT MAX(TaskID) as TaskID FROM Tasks";
        try {
            maxTaskID = jdbcTemplate.queryForObject(sql, Integer.class);
        }
        catch (Exception e){
            maxTaskID = 0;
        }
    }


    public boolean createTask(List<Integer> contactIDs, List<String> taskNotes, int accountID, String taskName) {
        // return the Task ID.
        try {
           String sql = "INSERT INTO Tasks(TaskID, AccountID, TaskName) VALUES (?, ?, ?)";
           int taskID = ++maxTaskID;
           jdbcTemplate.update(sql, taskID, accountID, taskName);
            System.out.println(contactIDs);
            System.out.println(taskName);
            System.out.println(taskNotes);
           // add the contacts.
            for(Iterator<Integer> iter = contactIDs.iterator(); iter.hasNext(); ){
                Integer contactID = Integer.parseInt(String.valueOf(iter.next()));
                addTaskContact(taskID, contactID);
            }

            for(Iterator<String> iter = taskNotes.iterator(); iter.hasNext(); ){
                String taskNote = iter.next();
                addTaskNote(taskID, taskNote);
            }

            return true;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }


    public boolean addTaskNote(int taskID, String taskNoteID) {
        try {
            String sql = "INSERT INTO TaskNotes(TaskID, TaskNoteID) VALUES (?, ?)";
            jdbcTemplate.update(sql, taskID, taskNoteID);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean addTaskContact(int taskID, int contactID) {
        try {
            String sql = "INSERT INTO Account_Contacts_Tasks(TaskId, ContactID) VALUES (?, ?)";
            jdbcTemplate.update(sql, taskID, contactID);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    public boolean updateTask(int taskID, String newTaskName) {
        try {
            String sql = "UPDATE Tasks SET TaskName = ? WHERE taskID = ?";
            jdbcTemplate.update(sql, newTaskName, taskID);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean updateTaskNote(int taskID, String oldTaskNoteID, String newTaskNoteID) {
        try {
            // delete the old one and create a new one.
            addTaskNote(taskID, newTaskNoteID);
            deleteTaskNote(taskID, oldTaskNoteID);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



    public boolean deleteTask(int taskID) {
        try {
            // delete the TaskNotes.
            String sql = "DELETE FROM TaskNotes WHERE TaskID = ?";
            jdbcTemplate.update(sql, taskID);
            // delete the TaskContacts
            sql = "DELETE FROM Account_Contacts_Tasks WHERE TaskID = ?";
            jdbcTemplate.update(sql, taskID);
            // delete the Task
            sql = "DELETE FROM Tasks WHERE TaskID = ?";
            System.out.println("DELETING TASK ID: " + taskID);
            jdbcTemplate.update(sql, taskID);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    public boolean deleteTaskNote(int taskID, String noteID) {
        try {
            String sql = "DELETE FROM TaskNotes WHERE TaskID = ? AND TaskNoteID = ?";
            jdbcTemplate.update(sql, taskID, noteID);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean deleteTaskContact(int taskID, int contactID) {
        try {
            String sql = "DELETE FROM Account_Contacts_Tasks WHERE TaskID = ? AND ContactID = ?";
            jdbcTemplate.update(sql, taskID, contactID);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Task> getTasksByAccountID(int accountID){
        try {
            String sql = "SELECT * FROM Tasks WHERE AccountID = ?";
            List<Task> tasks = jdbcTemplate.query(sql, taskRowMapper, accountID);
            ArrayList<Task> output = new ArrayList<>();

            for(Task task : tasks){
                int taskID = Integer.parseInt(String.valueOf(task.getTaskID()));
                sql = "SELECT * FROM TaskNotes WHERE TaskID = ? ";
                List<TaskNote> taskNoteList = jdbcTemplate.query(sql, taskNoteRowMapper, taskID);
                task.setTaskNoteList(taskNoteList);
                sql = "SELECT * FROM Account_Contacts_Tasks WHERE TaskID = ?";
                List<TaskContactAccount> taskContactAccounts = jdbcTemplate.query(sql, taskContactMapper, taskID);
                task.setTaskContactAccounts(taskContactAccounts);
            }
            return tasks;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public Task getTaskByID(int taskID) {
        try {
            // add in the Main task properties
            String sql = "SELECT * FROM Task WHERE TaskID = ?";
            Task task = jdbcTemplate.queryForObject(sql, taskRowMapper, taskID);
            // add in the task notes
            sql = "SELECT * FROM TaskName WHERE TaskID = ? ";
            List<TaskNote> taskNoteList = jdbcTemplate.query(sql, taskNoteRowMapper, taskID);
            task.setTaskNoteList(taskNoteList);
            // add in the task contacts
            sql = "SELECT * FROM Account_Contacts_Tasks WHERE TaskID = ?";
            List<TaskContactAccount> taskContactAccounts = jdbcTemplate.query(sql, taskContactMapper, taskID);
            task.setTaskContactAccounts(taskContactAccounts);
            return task;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
