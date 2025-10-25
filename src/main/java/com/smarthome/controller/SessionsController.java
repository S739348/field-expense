package com.smarthome.controller;

import com.smarthome.Service.UserService;
import com.smarthome.Service.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
public class SessionsController {

    @Autowired
    private UserSessionService userSessionService;
    @Autowired
    private UserService userService;

    @GetMapping("/activeCount")
    public ResponseEntity<?> getActiveCount() {

        int onlineCount = userSessionService.getAllOnlineUsers().size();
        int activeSessionCount = userSessionService.getActiveSessionCount();
        System.out.println("Online users: " + onlineCount + ", Active sessions: " + activeSessionCount);
        return ResponseEntity.ok(java.util.Map.of("activeCount", activeSessionCount));
    }

    @GetMapping("")
    public ResponseEntity<?> getSession(
            @RequestParam(required = true) Long userId,
            @RequestParam(required = false) String range
    ){
        try {

            return userService.getSessions(userId, range);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }






}
