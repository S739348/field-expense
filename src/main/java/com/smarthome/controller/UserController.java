package com.smarthome.controller;

import com.smarthome.Service.UserService;
import com.smarthome.dto.LoginRequest;
import com.smarthome.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("*")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user, @RequestHeader(value = "X-User-Id", required = false) Long actingUserId) {
        System.out.println(actingUserId);
        return userService.createUser(user, actingUserId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user, @RequestHeader(value = "X-User-Id", required = false) Long actingUserId) {
        return userService.updateUser(id, user, actingUserId);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteUsers(@RequestBody List<Long> ids, @RequestHeader(value = "X-User-Id", required = false) Long actingUserId) {
        System.out.println(ids.get(0));
        return userService.deleteUsers(ids, actingUserId);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }

}
