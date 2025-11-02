package com.aarya.fieldemployee.controller;

import com.aarya.fieldemployee.dtorequest.TaskCreateRequest;
import com.aarya.fieldemployee.dtorequest.TaskUpdateRequest;
import com.aarya.fieldemployee.dtorequest.TaskDeleteRequest;
import com.aarya.fieldemployee.dtoresponse.TaskResponse;
import com.aarya.fieldemployee.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/create")
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskCreateRequest request,
            @RequestHeader(name = "x-user-id", required = true) UUID requestingUserId) {
        TaskResponse response = taskService.createTask(request, requestingUserId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<TaskResponse> updateTask(
            @Valid @RequestBody TaskUpdateRequest request,
            @RequestHeader(name = "x-user-id", required = true) UUID requestingUserId) {
        TaskResponse response = taskService.updateTask(request, requestingUserId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteTasks(
            @Valid @RequestBody TaskDeleteRequest request,
            @RequestHeader(name = "x-user-id", required = true) UUID requestingUserId) {
        taskService.deleteTasks(request, requestingUserId);
        return ResponseEntity.ok("Tasks deleted successfully");
    }

    @GetMapping("/show")
    public ResponseEntity<List<TaskResponse>> showTasks(
            @RequestHeader(name = "x-user-id", required = true) UUID requestingUserId,  @RequestParam(required = false) String range) {
        List<TaskResponse> response = taskService.showTasks(requestingUserId,range);
        return ResponseEntity.ok(response);
    }
}
