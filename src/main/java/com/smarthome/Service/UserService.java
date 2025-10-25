package com.smarthome.Service;
import com.smarthome.dto.LoginRequest;
import com.smarthome.dto.SessionResponseDTO;
import com.smarthome.model.Session;
import com.smarthome.model.User;
import com.smarthome.repository.SessionRepository;
import com.smarthome.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionRepository sessionRepository;


    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
    
    public List<User> getUsersByRole(User.Role role) {
        return userRepository.findByRole(role);
    }
    
    public List<User> getSubordinates(Long managerId) {
        return userRepository.findByManagerUserId(managerId);
    }
    
    public User findById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
    
    public boolean assignManager(Long employeeId, Long managerId) {
        try {
            User employee = userRepository.findById(employeeId).orElse(null);
            User manager = managerId != null ? userRepository.findById(managerId).orElse(null) : null;
            
            if (employee != null) {
                employee.setManager(manager);
                userRepository.save(employee);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public ResponseEntity<User> getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(user))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null));
    }


    public ResponseEntity<?> createUser(User user, Long actingUserId) {

        if (!isAdminOrHr(actingUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only ADMIN or HR can create users");
        }

        if (user == null || user.getEmail() == null || user.getName() == null || user.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Missing required fields: name, email, password");
        }

        if (!user.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid email format");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already exists!");
        }

        if (user.getManager() != null && user.getManager().getUserId() != null) {
            User manager = userRepository.findById(user.getManager().getUserId())
                    .orElseThrow(() -> new RuntimeException("Manager not found with ID: " + user.getManager().getUserId()));
            user.setManager(manager);
        }

        User savedUser = userRepository.save(user);

        savedUser.setPassword(null);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }


    public ResponseEntity<?> updateUser(Long id, User updatedUser, Long actingUserId) {
        return userRepository.findById(id)
                .<ResponseEntity<?>>map(existing -> {

                    // ✅ Restrict role/status update to ADMIN or HR
                    if (updatedUser.getRole() != null || updatedUser.getStatus() != null) {
                        if (!isAdminOrHr(actingUserId)) {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                    .body("Only ADMIN or HR can change role or status");
                        }
                        if (updatedUser.getRole() != null)
                            existing.setRole(updatedUser.getRole());
                        if (updatedUser.getStatus() != null)
                            existing.setStatus(updatedUser.getStatus());
                    }

                    if (updatedUser.getName() != null)
                        existing.setName(updatedUser.getName());

                    if (updatedUser.getEmail() != null) {
                        if (!updatedUser.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body("Invalid email format");
                        }
                        existing.setEmail(updatedUser.getEmail());
                    }

                    if (updatedUser.getManager() != null) {
                        Long managerId = updatedUser.getManager().getUserId();
                        if (managerId != null) {
                            if (managerId.equals(id)) {
                                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body("A user cannot be their own manager");
                            }

                            User manager = userRepository.findById(managerId)
                                    .orElseThrow(() -> new RuntimeException("Manager not found with ID: " + managerId));
                            existing.setManager(manager);
                        } else {
                            existing.setManager(null); // allow removing manager
                        }
                    }

                    User saved = userRepository.save(existing);
                    saved.setPassword(null); // don’t expose password
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

   /*public ResponseEntity<?> getSessions(Long userId, String range) {
        LocalDateTime startDate;
        LocalDateTime endDate;

        if (range != null && !range.trim().isEmpty()) {
            try {
                String[] dateParts = range.split(" - ");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                startDate = LocalDate.parse(dateParts[0].trim(), formatter).atStartOfDay();
                endDate = LocalDate.parse(dateParts[1].trim(), formatter).atTime(23, 59, 59);
            } catch (Exception e) {
                endDate = LocalDateTime.now();
                startDate = endDate.minusDays(30);
            }
        } else {
            endDate = LocalDateTime.now();
            startDate = endDate.minusDays(30);
        }

        List<Session> listSession = sessionRepository
                .findByUserAndStartTimeRange(userId, startDate, endDate);

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("range", range);
        response.put("sessions", listSession);

        return ResponseEntity.ok(response);
    }*/
   public ResponseEntity<?> getSessions(Long userId, String range) {
       User currentUser = userRepository.findById(userId)
               .orElseThrow(() -> new RuntimeException("User not found"));

       LocalDateTime startDate;
       LocalDateTime endDate;

       // Parse range
       if (range != null && !range.trim().isEmpty()) {
           try {
               String[] dateParts = range.split(" - ");
               DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
               startDate = LocalDate.parse(dateParts[0].trim(), formatter).atStartOfDay();
               endDate = LocalDate.parse(dateParts[1].trim(), formatter).atTime(23, 59, 59);
           } catch (Exception e) {
               endDate = LocalDateTime.now();
               startDate = endDate.minusDays(30);
           }
       } else {
           endDate = LocalDateTime.now();
           startDate = endDate.minusDays(30);
       }

       List<?> sessions;

       String role = currentUser.getRole().toString().toUpperCase();
       switch (role) {
           case "FIELD_EMPLOYEE_FULLTIME":
           case "FIELD_EMPLOYEE_VENDOR":
               sessions = sessionRepository.findByUserAndStartTimeRange(userId, startDate, endDate);
               break;

           case "MANAGER":
               List<User> subordinates = userRepository.findByManagerUserId(userId);
               List<Long> subordinateIds = subordinates.stream()
                       .map(User::getUserId)
                       .collect(Collectors.toList());

               // optional: include manager's own sessions
               subordinateIds.add(userId);

               sessions = sessionRepository.findByUserIdsAndStartTimeRange(subordinateIds, startDate, endDate);
               break;

           case "ADMIN":
           case "HR":
           case "FINANCE":
               sessions = sessionRepository.findAllByStartTimeRange(startDate, endDate);
               break;

           default:
               return ResponseEntity.status(HttpStatus.FORBIDDEN)
                       .body(Map.of("error", "You are not authorized to view sessions"));
       }

       Map<String, Object> response = new HashMap<>();
       response.put("userId", currentUser.getUserId());
       response.put("role", currentUser.getRole());
       response.put("range", range != null ? range : "Last 30 days");
       response.put("sessionCount", sessions.size());
       response.put("sessions", sessions);

       return ResponseEntity.ok(response);
   }


}
