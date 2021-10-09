package com.noagility.personalcrm.controller;


import com.noagility.personalcrm.Util.JwtTokenUtil;
import com.noagility.personalcrm.model.Task;
import com.noagility.personalcrm.service.ContactService;
import com.noagility.personalcrm.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // used to get the account ID from the token
    @Autowired
    private ContactService contactService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @RequestMapping(
            value = "/createTask",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> create(@RequestBody Map<String, Object> payload, @CookieValue("jwt") String token) throws Exception {
        try {
            int accountID = contactService.getIDFromUsername(jwtTokenUtil.getUsernameFromToken(token));
            if(taskService.createTask(
                    (List<Integer>)payload.get("contactIDs"),
                    (List<String>)payload.get("taskNotes"),
                    accountID,

                    (String) payload.get("taskName"),
                    payload.containsKey("priority") ? (Integer) payload.get("priority") : -1,
                    payload.containsKey("deadline") ? (String) payload.get("deadline") : ""
            )){
                return ResponseEntity.ok().body("Success");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().body("Failure");
    }

    @RequestMapping(
            value = "/addTaskNote",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )

    public ResponseEntity<String> addTaskNote(@RequestBody Map<String, Object> payload, @CookieValue("jwt") String token){
        try {
            if(taskService.addTaskNote(
                    (Integer) payload.get("taskID"),
                    (String)payload.get("taskNote")
            )){
                return ResponseEntity.ok().body("Success");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().body("Failure");
    }

    @RequestMapping(
            value = "/addTaskContact",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> addTaskContact(@RequestBody Map<String, Object> payload, @CookieValue("jwt") String token){
        try {
            if(taskService.addTaskContact(
                    (Integer) payload.get("taskID"),
                    (Integer) payload.get("contactID")
            )){
                return ResponseEntity.ok().body("Success");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().body("Failure");
    }


    @RequestMapping(
            value = "/readTasks",
            method = RequestMethod.GET
    )
    public ResponseEntity<List<Task>> read(@CookieValue("jwt") String token){
        try {
            int accountID = contactService.getIDFromUsername(jwtTokenUtil.getUsernameFromToken(token));
            return ResponseEntity.ok().body(
                    taskService.getTasksByAccountID(accountID, false)
            );
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(
            value = "/readAllTasks",
            method = RequestMethod.GET
    )
    public ResponseEntity<List<Task>> readAll(@CookieValue("jwt") String token){
        try {
            int accountID = contactService.getIDFromUsername(jwtTokenUtil.getUsernameFromToken(token));
            return ResponseEntity.ok().body(
                    taskService.getTasksByAccountID(accountID, true)
            );
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    @RequestMapping(
            value = "/readTask",
            method = RequestMethod.GET
    )
    public ResponseEntity<Task> readTaskByID(@RequestBody Map<String, Object> payload, @CookieValue("jwt") String token){
        try {
            return ResponseEntity.ok().body(
                    taskService.getTaskByID((Integer) payload.get("taskID"), true)
            );
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }





    @RequestMapping(
            value = "/updateTask",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> updateTask(@RequestBody Map<String, Object> payload, @CookieValue("jwt") String token){
        try {
            if(taskService.updateTask(
                    (Integer) payload.get("taskID"),
                    (String) payload.get("newTaskName")
            )){
                return ResponseEntity.ok().body("Success");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().body("Failure");
    }

    @RequestMapping(
            value = "/updateTaskNote",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> updateTaskNote(@RequestBody Map<String, Object> payload, @CookieValue("jwt") String token){
        try {
            if(taskService.updateTaskNote(
                    (Integer) payload.get("taskNoteID"),
                    (String) payload.get("newTaskNote")
            )){
                return ResponseEntity.ok().body("Success");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().body("Failure");
    }


    @RequestMapping(
            value = "/updatePriority",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> updatePriority(@RequestBody Map<String, Object> payload, @CookieValue("jwt") String token){
        try {
            if(taskService.updatePriority(
                    (Integer) payload.get("taskID"),
                    (Integer) payload.get("priority")
            )){
                return ResponseEntity.ok().body("Success");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().body("Failure");
    }


    @RequestMapping(
            value = "/updateDeadline",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> updateDeadline(@RequestBody Map<String, Object> payload, @CookieValue("jwt") String token){
        try {
            if(taskService.updateDeadline(
                    (Integer) payload.get("taskID"),
                    (String)payload.get("deadline")
            )){
                return ResponseEntity.ok().body("Success");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().body("Failure");
    }



    @RequestMapping(
            value = "/deleteTask",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> deleteTask(@RequestBody Map<String, Object> payload, @CookieValue("jwt") String token){
        try{
            if(taskService.deleteTask((Integer) payload.get("taskID"))){
                return ResponseEntity.ok().body("Success");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().body("Failure");
    }

    @RequestMapping(
            value = "/deleteContact",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> deleteContact(@RequestBody Map<String, Object> payload, @CookieValue("jwt") String token){
        try{
            if(taskService.deleteTaskContact(
                    (Integer) payload.get("taskID"),
                    (Integer) payload.get("contactID")
            )){
                return ResponseEntity.ok().body("Success");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().body("Failure");
    }

    @RequestMapping(
            value = "/deleteTaskNote",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> deleteTaskNote(@RequestBody Map<String, Object> payload, @CookieValue("jwt") String token){
        try{
            if(taskService.deleteTaskNote(
                    (Integer) payload.get("taskNoteID")
            )){
                return ResponseEntity.ok().body("Success");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().body("Success");
    }

    @RequestMapping(
            value = "/deletePriority",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> deletePriority(@RequestBody Map<String, Object> payload, @CookieValue("jwt") String token){
        try {
            if(taskService.deletePriority(
                    (Integer) payload.get("taskID")
            )){
                return ResponseEntity.ok().body("Success");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().body("Failure");
    }

    @RequestMapping(
            value = "/deleteDeadline",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> deleteDeadline(@RequestBody Map<String, Object> payload, @CookieValue("jwt") String token){
        try {
            if(taskService.deleteDeadline(
                    (Integer) payload.get("taskID")
            )){
                return ResponseEntity.ok().body("Success");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().body("Failure");
    }

    @RequestMapping(
            value = "/completeTask",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> completeTask(@RequestBody Map<String, Object> payload, @CookieValue("jwt") String token){
        try{
            if(taskService.completeTask((Integer) payload.get("taskID"))){
                return ResponseEntity.ok().body("Success");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().body("Failure");
    }

}