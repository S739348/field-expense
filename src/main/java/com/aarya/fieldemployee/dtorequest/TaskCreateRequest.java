package com.aarya.fieldemployee.dtorequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public class TaskCreateRequest {

    @NotBlank(message = "Title is required")
    private String title;
    private String description="";
    @NotNull(message = "Employee ID is required")
    private UUID employeeId;
    private UUID managerId;

    public @NotBlank(message = "Title is required") String getTitle() {
        return title;
    }

    public void setTitle(@NotBlank(message = "Title is required") String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public @NotNull(message = "Employee ID is required") UUID getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(@NotNull(message = "Employee ID is required") UUID employeeId) {
        this.employeeId = employeeId;
    }

    public UUID getManagerId() {
        return managerId;
    }

    public void setManagerId(UUID managerId) {
        this.managerId = managerId;
    }
}
