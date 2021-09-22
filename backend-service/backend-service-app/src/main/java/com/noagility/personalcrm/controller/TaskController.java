package com.noagility.personalcrm.controller;


import com.noagility.personalcrm.model.Task;
import com.noagility.personalcrm.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;


    @RequestMapping(
            value = "/createTask",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> create(@RequestBody Map<String, Object> payload){
        try {
            if(taskService.createTask(
                    (List<Integer>)payload.get("contactIDs"),
                    (List<String>)payload.get("taskNotes"),
                    (Integer) payload.get("accountID"),
                    (String) payload.get("taskName")
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
    public ResponseEntity<String> addTaskNote(@RequestBody Map<String, Object> payload){
        try {
            if(taskService.addTaskNote(
                    Integer.parseInt((String)payload.get("taskID")),
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
    public ResponseEntity<String> addTaskContact(@RequestBody Map<String, Object> payload){
        try {
            if(taskService.addTaskContact(
                    Integer.parseInt((String)payload.get("taskID")),
                    Integer.parseInt((String)payload.get("contactID"))
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
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<Task>> read(@RequestBody Map<String, Object> payload){
        return ResponseEntity.ok().body(
                taskService.getTasksByAccountID(Integer.parseInt((String) payload.get("accountID")))
        );
    }

    @RequestMapping(
            value = "/updateTask",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<String> updateTask(@RequestBody Map<String, Object> payload){
        try {
            if(taskService.updateTask(
                    Integer.parseInt((String)payload.get("taskID")),
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
    public ResponseEntity<String> updateTaskNote(@RequestBody Map<String, Object> payload){
        try {
            if(taskService.updateTaskNote(
                    Integer.parseInt((String)payload.get("taskID")),
                    (String) payload.get("oldTaskNoteID"),
                    (String) payload.get("newTaskNoteID")
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
    public ResponseEntity<String> deleteTask(@RequestBody Map<String, Object> payload){
        try{
            if(taskService.deleteTask(Integer.parseInt((String)payload.get("taskID")))){
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
    public ResponseEntity<String> deleteContact(@RequestBody Map<String, Object> payload){
        try{
            if(taskService.deleteTaskContact(
                    Integer.parseInt((String)payload.get("taskID")),
                            Integer.parseInt((String)payload.get("contactID"))
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
    public ResponseEntity<String> deleteTaskNote(@RequestBody Map<String, Object> payload){
        try{
            if(taskService.deleteTaskNote(
                    Integer.parseInt((String)payload.get("taskID")),
                    (String)payload.get("noteID")
            )){
                return ResponseEntity.ok().body("Success");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().body("Success");
    }

}
