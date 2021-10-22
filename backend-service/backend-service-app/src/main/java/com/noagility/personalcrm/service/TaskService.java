package com.noagility.personalcrm.service;

import com.noagility.personalcrm.mapper.TaskContactRowMapper;
import com.noagility.personalcrm.mapper.TaskNoteRowMapper;
import com.noagility.personalcrm.mapper.TaskRowMapper;
import com.noagility.personalcrm.model.Task;
import com.noagility.personalcrm.model.TaskContactAccount;
import com.noagility.personalcrm.model.TaskNote;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Slf4j
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
    TaskContactRowMapper taskContactRowMapper;

    private int maxTaskID;
    private int maxTaskNoteID;

    @EventListener(ApplicationReadyEvent.class)
    private void loadTaskID(){
        String sql = "SELECT MAX(TaskID) as TaskID FROM Tasks";
        String sql2 = "SELECT MAX(TaskNoteID) as TaskID FROM TaskNotes";
        try {
            maxTaskID = jdbcTemplate.queryForObject(sql, Integer.class);
        } catch (Exception e){
            maxTaskID = 0;
        }
        try {
            maxTaskNoteID = jdbcTemplate.queryForObject(sql2, Integer.class);
        } catch (Exception e) {
            maxTaskNoteID = 0;
        }
        log.info("TaskService has started, incrementing TaskID counter from {} and TaskNoteID counter from {}", maxTaskID, maxTaskNoteID);
    }


    public boolean createTask(List<Integer> contactIDs, List<String> taskNotes, int accountID, String taskName, int priority, String deadline) {
        // return the Task ID.
        try {
            // when both priority and deadline are provided.
            int taskID = ++maxTaskID;
            String sql;
            if(priority >= 0 && !deadline.isBlank()){
                sql = "INSERT INTO Tasks(TaskID, AccountID, TaskName, TaskDeadline, TaskPriority) VALUES (?, ?, ?, ?, ?)";
                if (jdbcTemplate.update(sql, taskID, accountID, taskName, deadline, priority) == 0) {
                    throw new Exception("Failed to insert task into database");
                }
            }
            else if(priority >= 0){
                sql = "INSERT INTO Tasks(TaskID, AccountID, TaskName, TaskPriority) VALUES (?, ?, ?, ?)";
                if (jdbcTemplate.update(sql, taskID, accountID, taskName, priority) == 0) {
                    throw new Exception("Failed to insert task into database");
                }
            }
            else if(!deadline.isBlank()){
                sql = "INSERT INTO Tasks(TaskID, AccountID, TaskName, TaskDeadline, TaskPriority) VALUES (?, ?, ?, ?, ?)";
                if (jdbcTemplate.update(sql, taskID, accountID, taskName, deadline, -1) == 0) {
                    throw new Exception("Failed to insert task into database");
                }
            }
            else{
                sql = "INSERT INTO Tasks(TaskID, AccountID, TaskName, TaskPriority) VALUES (?, ?, ?, ?)";
                if (jdbcTemplate.update(sql, taskID, accountID, taskName, -1) == 0) {
                    throw new Exception("Failed to insert task into database");
                };
            }

            // add the contacts.
            if (contactIDs != null) {
                for(Iterator<Integer> iter = contactIDs.iterator(); iter.hasNext(); ){
                    Integer contactID = Integer.parseInt(String.valueOf(iter.next()));
                    addTaskContact(taskID, contactID);
                }
            }

            if (taskNotes != null) {
                for(Iterator<String> iter = taskNotes.iterator(); iter.hasNext(); ){
                    String taskNote = iter.next();
                    addTaskNote(taskID, taskNote);
                }
            }
            log.info("Task created for account (id: {}) - TaskID: {}, details: Name={}, Priority={}, Deadline={}, Contacts involved={}", accountID, taskID, taskName, priority, deadline, Arrays.toString(contactIDs.toArray()));
            return true;
        }
        catch(Exception e){
            log.error("Failed to create task for account (id: {}) with details: Name={}, Priority={}, Deadline={}, Contacts involved={}", accountID, taskName, priority, deadline, Arrays.toString(contactIDs.toArray()), e);
        }
        return false;
    }


    public boolean addTaskNote(int taskID, String taskNote) {
        try {
            String sql = "INSERT INTO TaskNotes(TaskID, TaskNoteID, Note) VALUES (?, ?, ?)";
            if (jdbcTemplate.update(sql, taskID, ++maxTaskNoteID, taskNote) == 0) {
                throw new Exception("Could not find Task from given TaskID");
            }
            log.info("Task note created for task (id: {}), TaskNoteID: {}", taskID, maxTaskNoteID);
            return true;
        }
        catch (Exception e) {
            log.error("Failed to create task note for task (id: {})", taskID, e);
        }
        return false;
    }


    public boolean addTaskContact(int taskID, int contactID) {
        try {
            String sql = "INSERT INTO Account_Contacts_Tasks(TaskId, ContactID) VALUES (?, ?)";
            if (jdbcTemplate.update(sql, taskID, contactID) == 0) {
                throw new Exception("Could not find Task from given TaskID");
            }
            log.info("Adding accountID {} to task {} as contact", contactID, taskID);
            return true;
        }
        catch (Exception e){
            log.error("Failed to add contact (id: {}) to task (id: {})", contactID, taskID);
        }
        return false;
    }


    public boolean updateTask(int taskID, String newTaskName) {
        try {
            String sql = "UPDATE Tasks SET TaskName = ? WHERE TaskID = ?";
            if (jdbcTemplate.update(sql, newTaskName, taskID) == 0) {
                throw new Exception("Could not find Task from given TaskID");
            }
            log.info("Updated task name for taskID (id: {}) to {}", taskID, newTaskName);
            return true;
        }
        catch (Exception e) {
            log.error("Failed to update task (id: {}) with new name: {}", taskID, newTaskName, e);
        }
        return false;
    }


    public boolean updateTaskNote( int taskNoteID, String newTaskNoteID) {
        try {
            // delete the old one and create a new one.
            String sql = "UPDATE TaskNotes SET Note = ? WHERE TaskNoteID = ?";
            if (jdbcTemplate.update(sql, newTaskNoteID, taskNoteID) == 0) {
                throw new Exception("Could not find Task from given TaskID");
            }
            log.info("Updated task note for task note (id: {}), new note: {}", taskNoteID, newTaskNoteID);
            return true;
        }
        catch (Exception e) {
            log.error("Failed to update taskNote (id: {}) with new name: {}", taskNoteID, newTaskNoteID, e);
        }
        return false;
    }

    public boolean updatePriority(int taskID, int newPriority){
        try{
            String sql = "UPDATE Tasks SET TaskPriority = ? WHERE TaskID = ?";
            if (jdbcTemplate.update(sql, newPriority, taskID) == 0) {
                throw new Exception("Could not find Task from given TaskID");
            }
            log.info("Updated priority for task (id: {}), new priority: {}", taskID, newPriority);
            return true;
        }
        catch (Exception e){
            log.error("Failed to update task (id: {}) with new priority: {}", taskID, newPriority, e);
        }
        return false;
    }

    public boolean updateDeadline(int taskID, String newDeadline){
        try{
            String sql = "UPDATE Tasks SET TaskDeadline = ? WHERE TaskID = ?";
            if (jdbcTemplate.update(sql, newDeadline, taskID) == 0) {
                throw new Exception("Could not find Task from given TaskID");
            }
            log.info("Updated deadline for task (id: {}), new deadline: {}", taskID, newDeadline);
            return true;
        }
        catch (Exception e){
            log.error("Failed to update task (id: {}) with new deadline: {}", taskID, newDeadline, e);
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
            if (jdbcTemplate.update(sql, taskID) == 0) {
                throw new Exception("TaskID does not exist");
            };
            log.info("Deleted task (id: {})", taskID);
            return true;
        }
        catch (Exception e){
            log.error("Failed to delete task (id: {})", taskID, e);
        }
        return false;
    }


    public boolean deleteTaskNote(int taskNoteID) {
        try {
            String sql = "DELETE FROM TaskNotes WHERE TaskNoteID = ?";
            if (jdbcTemplate.update(sql, taskNoteID) == 0) {
                throw new Exception("Could not find Task from given TaskID");
            }
            log.info("Deleted task note (id: {})", taskNoteID);
            return true;
        }
        catch (Exception e) {
            log.error("Failed to delete task note (id: {})", taskNoteID, e);
        }
        return false;
    }


    public boolean deleteTaskContact(int taskID, int contactID) {
        try {
            String sql = "DELETE FROM Account_Contacts_Tasks WHERE TaskID = ? AND ContactID = ?";
            if (jdbcTemplate.update(sql, taskID, contactID) == 0) {
                throw new Exception("Could not find Task from given TaskID");
            }
            log.info("Deleted task contact (id: {}) from task (id: {})", contactID, taskID);
            return true;
        }
        catch (Exception e) {
            log.error("Failed to delete contact (id: {}) from task (id: {})", taskID, contactID, e);
        }
        return false;
    }

    public boolean deletePriority(int taskID){
        try {
            String sql = "UPDATE Tasks SET TaskDeadline = NULL WHERE TaskID = ?";
            if (jdbcTemplate.update(sql, taskID) == 0) {
                throw new Exception("Could not find Task from given TaskID");
            };
            return true;
        }
        catch (Exception e){
            log.error("Failed to delete task (id: {}) priority", taskID, e);
        }
        return false;
    }

    public boolean deleteDeadline(int taskID){
        try {
            String sql = "UPDATE Tasks SET TaskDeadline = NULL WHERE TaskID = ?";
            if (jdbcTemplate.update(sql, taskID) == 0) {
                throw new Exception("Could not find Task from given TaskID");
            }
            log.info("Deleted deadline for task (id: {}", taskID);
            return true;
        }
        catch (Exception e){
            log.error("Failed to delete task (id: {}) deadline", taskID, e);
        }
        return false;
    }


    public List<Task> getTasksByAccountID(int accountID, Boolean readAll){
        List<Task> tasks = new ArrayList<>();
        for(Task task: getTasksOwnedByAccountID(accountID, readAll)){
            tasks.add(task);
        }
        for(Task task: getTasksWithAccountID(accountID, readAll)){
            tasks.add(task);
        }
        return tasks;
    }

    private List<Task> getTasksOwnedByAccountID(int accountID, Boolean readAll){
        try {
            String sql;

            if(readAll){
                sql = "SELECT * FROM Tasks WHERE AccountID = ?";
            }
            else{
                sql = "SELECT * FROM Tasks WHERE AccountID = ? AND TaskComplete = 0";
            }

            List<Task> tasks = jdbcTemplate.query(sql, taskRowMapper, accountID);

            for(Task task : tasks){
                int taskID = Integer.parseInt(String.valueOf(task.getTaskID()));
                sql = "SELECT * FROM TaskNotes WHERE TaskID = ? ";
                List<TaskNote> taskNoteList = jdbcTemplate.query(sql, taskNoteRowMapper, taskID);
                task.setTaskNoteList(taskNoteList);
                task.setOwner(true);
                sql = "SELECT * FROM Account_Contacts_Tasks WHERE TaskID = ?";
                List<TaskContactAccount> taskContactAccounts = jdbcTemplate.query(sql, taskContactRowMapper, taskID);
                task.setTaskContactAccounts(taskContactAccounts);
            }
            return tasks;
        }
        catch (Exception e) {
            log.error("Failed to get tasks owned by account (id: {})", accountID, e);
        }
        return null;
    }

    private List<Task> getTasksWithAccountID(int accountID, Boolean readAll){
        try {
            String sql = "SELECT * FROM Account_Contacts_Tasks WHERE ContactID = ?";
            List<TaskContactAccount> accountsContactsTasks = jdbcTemplate.query(sql, taskContactRowMapper, accountID);
            List<Task> tasks = new ArrayList<>();
            for(TaskContactAccount taskContactAccount: accountsContactsTasks){
                int taskID = taskContactAccount.getTaskID();
                Task nextTask = getTaskByID(taskID, readAll);
                if(nextTask != null){
                    tasks.add(nextTask);
                }
            }
            return tasks;
        }
        catch (Exception e){
            log.error("Failed to get tasks with account (id: {})", accountID, e);
        }
        return null;
    }


    public Task getTaskByID(int taskID, Boolean readAll) {
        try {
            // add in the Main task properties
            String sql = "SELECT * FROM Tasks WHERE TaskID = ?";
            Task task = jdbcTemplate.queryForObject(sql, taskRowMapper, taskID);
            // condition to make sure the task is not complete.
            if(task.getTaskComplete() == 1 && !readAll){
                return null;
            }
            // add in the task notes
            sql = "SELECT * FROM TaskNotes WHERE TaskID = ?";
            List<TaskNote> taskNoteList = jdbcTemplate.query(sql, taskNoteRowMapper, taskID);
            task.setTaskNoteList(taskNoteList);
            task.setOwner(false);
            // add in the task contacts
            sql = "SELECT * FROM Account_Contacts_Tasks WHERE TaskID = ?";
            List<TaskContactAccount> taskContactAccounts = jdbcTemplate.query(sql, taskContactRowMapper, taskID);
            task.setTaskContactAccounts(taskContactAccounts);
            return task;
        }
        catch (Exception e) {
            log.error("Failed to get task by id {})", taskID, e);
        }
        return null;
    }

    public boolean completeTask(int taskID){
        try {
            String sql = "UPDATE Tasks SET TaskComplete = 1 WHERE TaskID = ?";
            jdbcTemplate.update(sql, taskID);
            log.info("Setting task (id: {}) as complete", taskID);
            return true;
        }
        catch (Exception e){
            log.error("Failed to set task (id: {}) as complete", taskID, e);
        }
        return false;
    }
}