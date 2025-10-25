package com.smarthome.dto;

import com.smarthome.model.User;

public class CreateUserRequest {
    private String name;
    private String email;
    private String password;
    private User.Role role;
    private User.Status status;
    private Long managerId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public User.Role getRole() { return role; }
    public void setRole(User.Role role) { this.role = role; }

    public User.Status getStatus() { return status; }
    public void setStatus(User.Status status) { this.status = status; }

    public Long getManagerId() { return managerId; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }
}