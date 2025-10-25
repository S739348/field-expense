package com.smarthome.controller;

import com.smarthome.Service.UserService;
import com.smarthome.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/role-based")
public class RoleBasedController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsersByRole(@RequestHeader("X-User-Id") Long userId) {
        User currentUser = userService.findById(userId);
        if (currentUser == null) {
            return ResponseEntity.badRequest().build();
        }

        List<User> users;
        switch (currentUser.getRole()) {
            case ADMIN:
            case HR:
                ResponseEntity<List<User>> allUsersResponse = userService.getAllUsers();
                users = allUsersResponse.getBody();
                break;
            case MANAGER:
                users = userService.getSubordinates(userId);
                break;
            default:
                return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/managers")
    public ResponseEntity<List<User>> getManagers() {
        List<User> managers = userService.getUsersByRole(User.Role.MANAGER);
        return ResponseEntity.ok(managers);
    }

    @PostMapping("/assign-manager")
    public ResponseEntity<String> assignManager(@RequestBody Map<String, Long> request, @RequestHeader("X-User-Id") Long adminId) {
        User admin = userService.findById(adminId);
        if (admin == null || (admin.getRole() != User.Role.ADMIN && admin.getRole() != User.Role.HR)) {
            return ResponseEntity.status(403).body("Only Admin/HR can assign managers");
        }

        Long employeeId = request.get("employeeId");
        Long managerId = request.get("managerId");

        boolean success = userService.assignManager(employeeId, managerId);
        return success ? ResponseEntity.ok("Manager assigned successfully") 
                      : ResponseEntity.badRequest().body("Failed to assign manager");
    }
}