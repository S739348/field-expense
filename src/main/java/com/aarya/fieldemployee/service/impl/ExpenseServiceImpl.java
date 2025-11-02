package com.aarya.fieldemployee.service.impl;

import com.aarya.fieldemployee.dtorequest.ExpenseCreateRequest;
import com.aarya.fieldemployee.dtorequest.ExpenseUpdateRequest;
import com.aarya.fieldemployee.dtorequest.ExpenseDeleteRequest;
import com.aarya.fieldemployee.dtoresponse.ExpenseResponse;
import com.aarya.fieldemployee.model.Employee;
import com.aarya.fieldemployee.model.Expense;
import com.aarya.fieldemployee.model.Task;
import com.aarya.fieldemployee.model.ExpenseCategory;
import com.aarya.fieldemployee.repository.EmployeeRepository;
import com.aarya.fieldemployee.repository.ExpenseRepository;
import com.aarya.fieldemployee.repository.TaskRepository;
import com.aarya.fieldemployee.repository.ExpenseCategoryRepository;
import com.aarya.fieldemployee.service.ExpenseService;
import com.aarya.fieldemployee.service.ImageService;
import com.aarya.fieldemployee.util.Authorized;
import com.aarya.fieldemployee.util.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ExpenseCategoryRepository categoryRepository;
    @Autowired
    private ImageService imageService;
    @Autowired
    private Authorized authorized;

    @Override
    @Transactional
    public ExpenseResponse createExpense(ExpenseCreateRequest request, UUID requestingUserId) {
        Employee employee = employeeRepository.findById(requestingUserId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (employee.getStatus() == Employee.Status.Inactive) {
            throw new RuntimeException("Account is inactive");
        }

        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        ExpenseCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Expense expense = new Expense();
        expense.setTask(task);
        expense.setEmployee(employee);
        expense.setCategory(category);
        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());

        if (request.getReceiptFile() != null && !request.getReceiptFile().isEmpty()) {
            try {
                String receiptUrl = imageService.saveProfileImage(request.getReceiptFile());
                expense.setReceiptUrl(receiptUrl);
            } catch (Exception e) {
                throw new RuntimeException("Failed to process receipt image", e);
            }
        }
        expense = expenseRepository.save(expense);
        return convertToResponse(expense);
    }

    @Override
    @Transactional
    public ExpenseResponse updateExpense(ExpenseUpdateRequest request, UUID requestingUserId) {
        Employee requestingEmployee = employeeRepository.findById(requestingUserId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Expense expense = expenseRepository.findById(request.getExpenseId())
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        Employee.Role role = requestingEmployee.getRole();

        switch (role) {
            case Field_Employee_Full_Time:
            case Field_Employee_Vendor:
                if (!expense.getEmployee().getEmployeeId().equals(requestingUserId)) {
                    throw new RuntimeException("You can only update your own expenses");
                }
                if (expense.getStatus() != Expense.Status.Pending && expense.getStatus() != Expense.Status.Rejected) {
                    throw new RuntimeException("Can only update pending or rejected expenses");
                }
                if (request.getAmount() != null) expense.setAmount(request.getAmount());
                if (request.getDescription() != null) expense.setDescription(request.getDescription());
                if (request.getReceiptFile() != null && !request.getReceiptFile().isEmpty()) {
                    updateReceiptFile(expense, request.getReceiptFile());
                }
                break;

            case Manager:

                if (expense.getPaymentStatus() != Expense.PaymentStatus.Pending) {
                    throw new RuntimeException("Cannot modify expense after payment processing started");
                }
                if (request.getStatus() != null) {
                    expense.setStatus(request.getStatus());
                    if (request.getStatus() == Expense.Status.Approved) {
                        expense.setApprovedBy(requestingEmployee);
                        expense.setApprovedAt(LocalDateTime.now());
                    }
                }
                break;

            case Finance:
                if (request.getStatus() != null) {
                    expense.setStatus(request.getStatus());
                    if (request.getStatus() == Expense.Status.Approved) {
                        expense.setApprovedBy(requestingEmployee);
                        expense.setApprovedAt(LocalDateTime.now());
                    }
                }
                if (request.getPaymentStatus() != null && (request.getPaymentStatus() != Expense.PaymentStatus.Paid)) {
                    expense.setPaymentStatus(request.getPaymentStatus());
                }
                break;

            case Admin:
                if (expense.getPaymentStatus() != Expense.PaymentStatus.Pending) {
                    throw new RuntimeException("Cannot modify expense after payment Payment");
                }

                if (request.getAmount() != null) expense.setAmount(request.getAmount());
                if (request.getDescription() != null) expense.setDescription(request.getDescription());
                if (request.getCategoryId() != null) {
                    ExpenseCategory category = categoryRepository.findById(request.getCategoryId())
                            .orElseThrow(() -> new RuntimeException("Category not found"));
                    expense.setCategory(category);
                }
                if (request.getStatus() != null) {
                    expense.setStatus(request.getStatus());
                    if (request.getStatus() == Expense.Status.Approved) {
                        expense.setApprovedBy(requestingEmployee);
                        expense.setApprovedAt(LocalDateTime.now());
                    }
                }
                if (request.getPaymentStatus() != null) {
                    expense.setPaymentStatus(request.getPaymentStatus());
                }
                if (request.getReceiptFile() != null && !request.getReceiptFile().isEmpty()) {
                    updateReceiptFile(expense, request.getReceiptFile());
                }
                break;

            default:
                throw new RuntimeException("Unauthorized role for expense update");
        }

        expense = expenseRepository.save(expense);
        return convertToResponse(expense);
    }

    private void updateReceiptFile(Expense expense, MultipartFile receiptFile) {
        try {
            String oldUrl = expense.getReceiptUrl();
            String receiptUrl = imageService.saveProfileImage(receiptFile);
            expense.setReceiptUrl(receiptUrl);
            if (oldUrl != null && !oldUrl.isEmpty()) {
                imageService.deleteProfileImage(oldUrl);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to process receipt image", e);
        }
    }

    @Override
    @Transactional
    public void deleteExpenses(ExpenseDeleteRequest request, UUID requestingUserId) {
        Employee requestingEmployee = employeeRepository.findById(requestingUserId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Employee.Role role = requestingEmployee.getRole();

        if (role != Employee.Role.Admin &&
                role != Employee.Role.Hr &&
                role != Employee.Role.Manager) {
            throw new RuntimeException("Unauthorized to delete expenses");
        }

        for (UUID expenseId : request.getExpenseIds()) {
            Expense expense = expenseRepository.findById(expenseId)
                    .orElseThrow(() -> new RuntimeException("Expense not found: " + expenseId));

            if (expense.getPaymentStatus() != Expense.PaymentStatus.Pending) {
                throw new RuntimeException("Cannot delete expense after payment processing started");
            }

            switch (role) {
                case Manager:
                    if (!expense.getTask().getManager().getEmployeeId().equals(requestingUserId)) {
                        throw new RuntimeException("Managers can only delete expenses from their own tasks");
                    }
                    break;

            }
            if (expense.getReceiptUrl() != null && !expense.getReceiptUrl().isEmpty()) {
                try {
                    imageService.deleteProfileImage(expense.getReceiptUrl());
                } catch (Exception e) {
                    System.err.println("Failed to delete receipt image: " + expense.getReceiptUrl());
                }
            }
        }
        expenseRepository.deleteAllById(request.getExpenseIds());
    }

    @Override
    public List<ExpenseResponse> showExpenses(UUID requestingUserId, String range) {
        Employee requestedUser = employeeRepository.findById(requestingUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String role = Role.getRole(requestedUser.getRole());
        List<Expense> expenses;
        LocalDateTime startDate;
        LocalDateTime endDate;

        if (range == null || range.isEmpty()) {
            endDate = LocalDateTime.now();
            startDate = endDate.minusDays(30);
        } else {
            try {
                String[] dates = range.split(" - ");
                if (dates.length != 2) {
                    throw new IllegalArgumentException("Invalid date range format");
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                startDate = LocalDate.parse(dates[0].trim(), formatter).atStartOfDay();
                endDate = LocalDate.parse(dates[1].trim(), formatter).atTime(23, 59, 59);

            } catch (Exception e) {
                endDate = LocalDateTime.now();
                startDate = endDate.minusDays(30);
            }
        }

        switch (role) {
            case "Field_Employee_Full_Time":
            case "Field_Employee_Vendor":
                expenses = expenseRepository.findByEmployeeEmployeeIdAndTaskCreatedAtBetween(requestingUserId, startDate, endDate);
                break;
            case "Manager":
                expenses = expenseRepository.findByTaskManagerEmployeeIdAndTaskCreatedAtBetween(requestingUserId, startDate, endDate);
                break;
            case "Hr":
            case "Finance":
                expenses = expenseRepository.findByTaskCreatedAtBetween(startDate, endDate);
                break;
            case "Admin":
                expenses = expenseRepository.findByTaskCreatedAtBetween(startDate, endDate);
                break;
            default:
                throw new IllegalArgumentException("Invalid or unauthorized role for this user");
        }


        return expenses.stream().map(this::convertToResponse).toList();
    }

    private ExpenseResponse convertToResponse(Expense expense) {
        ExpenseResponse response = new ExpenseResponse();
        response.setId(expense.getId());
        response.setTaskId(expense.getTask().getTaskId());
        response.setTaskTitle(expense.getTask().getTitle());
        response.setEmployeeId(expense.getEmployee().getEmployeeId());
        response.setEmployeeName(expense.getEmployee().getName());
        response.setManagerId(expense.getTask().getManager().getEmployeeId());
        response.setManagerName(expense.getTask().getManager().getName());
        response.setCategoryId(expense.getCategory().getId());
        response.setCategoryName(expense.getCategory().getName());
        response.setAmount(expense.getAmount());
        response.setDescription(expense.getDescription());
        response.setReceiptUrl(expense.getReceiptUrl());
        response.setStatus(expense.getStatus());
        if (expense.getApprovedBy() != null) {
            response.setApprovedBy(expense.getApprovedBy().getEmployeeId());
            response.setApproverName(expense.getApprovedBy().getName());
        }
        response.setPaymentStatus(expense.getPaymentStatus());
        response.setApprovedAt(expense.getApprovedAt());
        response.setCreatedAt(expense.getCreatedAt());
        response.setUpdatedAt(expense.getUpdatedAt());
        return response;

    }


}
