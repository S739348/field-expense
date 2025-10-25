package com.smarthome.controller;

import com.smarthome.Service.UserService;
import com.smarthome.Service.UserSessionService;
import com.smarthome.dto.ActiveUserDto;
import com.smarthome.dto.StartLongLat;
import com.smarthome.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/active-users")
public class ActiveUsersController {

    @Autowired
    private UserSessionService userSessionService;

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public ResponseEntity<List<ActiveUserDto>> getActiveUsers(@RequestHeader("X-User-Id") Long userId) {
        User currentUser = userService.findById(userId);
        if (currentUser == null) {
            return ResponseEntity.badRequest().build();
        }

        List<ActiveUserDto> activeUsers = new ArrayList<>();
        Map<String, StartLongLat> startLocations = userSessionService.getStartLongLat();

        for (Map.Entry<String, StartLongLat> entry : startLocations.entrySet()) {
            String userIdStr = entry.getKey();
            StartLongLat location = entry.getValue();
            
            try {
                Long activeUserId = Long.parseLong(userIdStr);
                User activeUser = userService.findById(activeUserId);
                
                if (activeUser != null && canViewUser(currentUser, activeUser)) {
                    ActiveUserDto dto = new ActiveUserDto();
                    dto.setUserId(activeUserId);
                    dto.setName(location.getName());
                    dto.setEmail(location.getEmail());
                    dto.setLat(location.getLat());
                    dto.setLng(location.getLang());
                    dto.setStatus(userSessionService.getActiveSession(userIdStr) != null ? "ACTIVE" : "INACTIVE");
                    
                    // Convert coordinates to place name (mock implementation)
                    dto.setPlaceName(getPlaceName(location.getLat(), location.getLang()));
                    
                    activeUsers.add(dto);
                }
            } catch (NumberFormatException e) {
                // Skip invalid user IDs
            }
        }

        return ResponseEntity.ok(activeUsers);
    }
    
    @GetMapping("/count")
    public ResponseEntity<Integer> getActiveUsersCount(@RequestHeader("X-User-Id") Long userId) {
        User currentUser = userService.findById(userId);
        if (currentUser == null) {
            return ResponseEntity.badRequest().build();
        }

        Map<String, StartLongLat> startLocations = userSessionService.getStartLongLat();
        int count = 0;

        for (Map.Entry<String, StartLongLat> entry : startLocations.entrySet()) {
            String userIdStr = entry.getKey();
            
            try {
                Long activeUserId = Long.parseLong(userIdStr);
                User activeUser = userService.findById(activeUserId);
                
                if (activeUser != null && canViewUser(currentUser, activeUser) && 
                    userSessionService.getActiveSession(userIdStr) != null) {
                    count++;
                }
            } catch (NumberFormatException e) {
                // Skip invalid user IDs
            }
        }

        return ResponseEntity.ok(count);
    }

    private boolean canViewUser(User currentUser, User targetUser) {
        switch (currentUser.getRole()) {
            case ADMIN:
            case HR:
            case FINANCE:
                return true;
            case MANAGER:
                return targetUser.getManager() != null && 
                       targetUser.getManager().getUserId().equals(currentUser.getUserId());
            default:
                return false;
        }
    }

    private String getPlaceName(double lat, double lng) {
        // Mock implementation - in real app, use reverse geocoding API
        return String.format("Location %.4f, %.4f", lat, lng);
    }
}