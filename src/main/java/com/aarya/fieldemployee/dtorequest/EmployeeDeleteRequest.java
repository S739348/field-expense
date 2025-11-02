package com.aarya.fieldemployee.dtorequest;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public class EmployeeDeleteRequest {
    
    
    @NotEmpty(message = "Employee IDs list cannot be empty")
    private List<UUID> employeeIds;

    public List<UUID> getEmployeeIds() {
        return employeeIds;
    }

    public void setEmployeeIds(List<UUID> employeeIds) {
        this.employeeIds = employeeIds;
    }
}