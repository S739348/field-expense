package com.aarya.fieldemployee.service.impl;

import com.aarya.fieldemployee.dtorequest.TaskCreateRequest;
import com.aarya.fieldemployee.dtorequest.TaskUpdateRequest;
import com.aarya.fieldemployee.dtorequest.TaskDeleteRequest;
import com.aarya.fieldemployee.dtoresponse.TaskResponse;
import com.aarya.fieldemployee.model.Employee;
import com.aarya.fieldemployee.model.Task;
import com.aarya.fieldemployee.repository.EmployeeRepository;
import com.aarya.fieldemployee.repository.TaskRepository;
import com.aarya.fieldemployee.service.TaskService;
import com.aarya.fieldemployee.util.Authorized;
import com.aarya.fieldemployee.util.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private Authorized authorized;

    @Override
    @Transactional
    public TaskResponse createTask(TaskCreateRequest request, UUID requestingUserId) {
        Employee manager = employeeRepository.findById(requestingUserId)
                .orElseThrow(() -> new RuntimeException("Manager not found"));
        authorized.validateManager(manager);

        Employee fieldEmployee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        authorized.validateEmplyoee(fieldEmployee);

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setEmployee(fieldEmployee);
        task.setManager(manager);

        task = taskRepository.save(task);
        return convertToResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse updateTask(TaskUpdateRequest request, UUID requestingUserId) {
        Employee requestingEmployee = employeeRepository.findById(requestingUserId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Employee.Role role = requestingEmployee.getRole();

        switch (role) {
            case Field_Employee_Full_Time:
            case Field_Employee_Vendor:

                if (!task.getEmployee().getEmployeeId().equals(requestingUserId)) {
                    throw new RuntimeException("You can only update your own tasks");
                }
                if (request.getStatus() != null) task.setStatus(request.getStatus());
                if (request.getStartTime() != null) task.setStartTime(request.getStartTime());
                if (request.getEndTime() != null) task.setEndTime(request.getEndTime());
                break;

            case Manager:
                // Managers can update tasks they created (reassign, modify details)
                if (!task.getManager().getEmployeeId().equals(requestingUserId)) {
                    throw new RuntimeException("You can only update tasks you created");
                }
                if (request.getTitle() != null) task.setTitle(request.getTitle());
                if (request.getDescription() != null) task.setDescription(request.getDescription());
                if (request.getStatus() != null) task.setStatus(request.getStatus());
                if (request.getEmployeeId() != null) {
                    Employee employee = employeeRepository.findById(request.getEmployeeId())
                            .orElseThrow(() -> new RuntimeException("Employee not found"));
                    task.setEmployee(employee);
                }
                break;

            case Admin:

                if (request.getTitle() != null) task.setTitle(request.getTitle());
                if (request.getDescription() != null) task.setDescription(request.getDescription());
                if (request.getStatus() != null) task.setStatus(request.getStatus());
                if (request.getStartTime() != null) task.setStartTime(request.getStartTime());
                if (request.getEndTime() != null) task.setEndTime(request.getEndTime());
                if (request.getEmployeeId() != null) {
                    Employee employee = employeeRepository.findById(request.getEmployeeId())
                            .orElseThrow(() -> new RuntimeException("Employee not found"));
                    task.setEmployee(employee);
                }
                if (request.getManagerId() != null) {
                    Employee manager = employeeRepository.findById(request.getManagerId())
                            .orElseThrow(() -> new RuntimeException("Manager not found"));
                    task.setManager(manager);
                }
                break;

            default:
                throw new RuntimeException("Unauthorized role for task update");
        }

        task = taskRepository.save(task);
        return convertToResponse(task);
    }


    @Override
    @Transactional
    public void deleteTasks(TaskDeleteRequest request, UUID requestingUserId) {
        Employee requestedUser = employeeRepository.findById(requestingUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Employee.Role role = requestedUser.getRole();
        switch (role) {
            case Manager:
            case Admin:
                taskRepository.deleteAllById(request.getTaskIds());
            default:
                throw new RuntimeException("Unauthorized role for delete task.");

        }

    }

    @Override
    public List<TaskResponse> showTasks(UUID requestingUserId, String range) {
        Employee requestedUser = employeeRepository.findById(requestingUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String role = Role.getRole(requestedUser.getRole());
        List<Task> tasks;

        // Calculate date range
        LocalDateTime startDate;
        LocalDateTime endDate;

        if (range == null || range.isEmpty()) {
            // Default: last 30 days
            endDate = LocalDateTime.now();
            startDate = endDate.minusDays(30);
        } else {
            try {
                // Parse date range format: "29-09-2025 - 31-10-2025"
                String[] dates = range.split(" - ");
                if (dates.length != 2) {
                    throw new IllegalArgumentException("Invalid date range format");
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                startDate = LocalDate.parse(dates[0].trim(), formatter).atStartOfDay();
                endDate = LocalDate.parse(dates[1].trim(), formatter).atTime(23, 59, 59);

            } catch (Exception e) {
                // If parsing fails, default to last 30 days
                endDate = LocalDateTime.now();
                startDate = endDate.minusDays(30);
            }
        }

        switch (role) {
            case "Field_Employee_Full_Time":
            case "Field_Employee_Vendor":
                tasks = taskRepository.findByEmployeeEmployeeIdAndCreatedAtBetween(requestingUserId, startDate, endDate);
                break;
            case "Manager":
                tasks = taskRepository.findByManagerEmployeeIdAndCreatedAtBetween(requestingUserId, startDate, endDate);
                break;
            case "Hr":
            case "Finance":
            case "Admin":
                tasks = taskRepository.findByCreatedAtBetween(startDate, endDate);
                break;
            default:
                throw new IllegalArgumentException("Invalid or unauthorized role for this user");
        }

        return tasks.stream().map(this::convertToResponse).toList();
    }

    private TaskResponse convertToResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setTaskId(task.getTaskId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setEmployeeId(task.getEmployee().getEmployeeId());
        response.setEmployeeName(task.getEmployee().getName());
        response.setManagerId(task.getManager().getEmployeeId());
        response.setManagerName(task.getManager().getName());
        response.setStatus(task.getStatus());
        response.setStartTime(task.getStartTime());
        response.setEndTime(task.getEndTime());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        return response;
    }
}
