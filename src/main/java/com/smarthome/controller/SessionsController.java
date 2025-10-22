package com.smarthome.controller;

import com.smarthome.Service.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sessions")
public class SessionsController {

    @Autowired
    private UserSessionService userSessionService;

    @GetMapping("/activeCount")
    public ResponseEntity<?> getActiveCount() {
        // Count only users who are both online AND have active sessions
        int onlineCount = userSessionService.getAllOnlineUsers().size();
        int activeSessionCount = userSessionService.getActiveSessionCount();
        System.out.println("Online users: " + onlineCount + ", Active sessions: " + activeSessionCount);
        return ResponseEntity.ok(java.util.Map.of("activeCount", activeSessionCount));
    }






}
