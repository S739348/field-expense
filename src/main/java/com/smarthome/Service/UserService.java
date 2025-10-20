package com.smarthome.Service;
import com.smarthome.dto.LoginRequest;
import com.smarthome.model.User;
import com.smarthome.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;



    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }


    public ResponseEntity<User> getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(user))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null));
    }


    public ResponseEntity<?> createUser(User user, Long actingUserId) {

        if (!isAdminOrHr(actingUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only ADMIN or HR can create users");
        }

        // basic validation
        if (user == null || user.getEmail() == null || user.getName() == null || user.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing required fields: name, email, password");
        }
        if (!user.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid email format");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already exists!");
        }
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }


    public ResponseEntity<?> updateUser(Long id, User updatedUser, Long actingUserId) {
        return userRepository.findById(id)
                .<ResponseEntity<?>>map(existing -> {
                    // Only ADMIN or HR can change role/status
                    if (updatedUser.getRole() != null || updatedUser.getStatus() != null) {
                        if (!isAdminOrHr(actingUserId)) {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only ADMIN or HR can change role or status");
                        }
                        if (updatedUser.getRole() != null) existing.setRole(updatedUser.getRole());
                        if (updatedUser.getStatus() != null) existing.setStatus(updatedUser.getStatus());
                    }
                    if (updatedUser.getName() != null) existing.setName(updatedUser.getName());
                    if (updatedUser.getEmail() != null) {
                        if (!updatedUser.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid email format");
                        }
                        existing.setEmail(updatedUser.getEmail());
                    }
                    User saved = userRepository.save(existing);
                    return ResponseEntity.ok(saved);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found with id: " + id));
    }



    public ResponseEntity<String> deleteUsers(List<Long> ids, Long actingUserId) {
        if (!isAdminOrHr(actingUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only ADMIN or HR can delete users");
        }

        List<Long> notFoundIds = new ArrayList<>();

        for (Long id : ids) {
            if (userRepository.existsById(id)) {
                userRepository.deleteById(id);
            } else {
                notFoundIds.add(id);
            }
        }

        if (!notFoundIds.isEmpty()) {
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .body("Some users not found: " + notFoundIds);
        }

        return ResponseEntity.ok("All users deleted successfully");
    }

    private boolean isAdminOrHr(Long actingUserId) {
        if (actingUserId == null) return false;
        return userRepository.findById(actingUserId)
                .map(u -> u.getRole() == User.Role.ADMIN || u.getRole() == User.Role.HR)
                .orElse(false);
    }

    public ResponseEntity<?> login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found with email: " + request.getEmail());
        }

        User user = userOpt.get();
        if (!user.getPassword().equals(request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid password");
        }

        if (user.getStatus() == User.Status.deactivated) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("User account is deactivated");
        }

        return ResponseEntity.ok(user);
    }

}
