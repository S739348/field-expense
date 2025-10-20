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
        int count = userSessionService.getAllOnlineUsers().size();
        return ResponseEntity.ok(java.util.Map.of("activeCount", count));
    }






}
