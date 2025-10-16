package com.smarthome.Service;

import com.smarthome.dto.ExpenseApprovalRequest;
import com.smarthome.model.Expense;
import com.smarthome.model.User;
import com.smarthome.model.Session;
import com.smarthome.model.ExpenseCategories;
import com.smarthome.repository.ExpenseRepository;
import com.smarthome.repository.UserRepository;
import com.smarthome.repository.SessionRepository;
import com.smarthome.repository.ExpenseCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private ExpenseCategoryRepository expenseCategoryRepository;

    public Expense createExpense(Expense expense) {
        // Fetch managed entities from DB
        User user = userRepository.findById(expense.getUser().getUser_id())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Session session = sessionRepository.findById(expense.getSession().getSession_id())
                .orElseThrow(() -> new RuntimeException("Session not found"));
        ExpenseCategories category = expenseCategoryRepository.findById(expense.getCategory().getCategory_id())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        expense.setUser(user);
        expense.setSession(session);
        expense.setCategory(category);

        // Optional approvers (fetch if present)
        if (expense.getPreApprover() != null && expense.getPreApprover().getUser_id() != null) {
            User preApprover = userRepository.findById(expense.getPreApprover().getUser_id())
                    .orElseThrow(() -> new RuntimeException("PreApprover not found"));
            expense.setPreApprover(preApprover);
        }

        if (expense.getHrApprover() != null && expense.getHrApprover().getUser_id() != null) {
            User hrApprover = userRepository.findById(expense.getHrApprover().getUser_id())
                    .orElseThrow(() -> new RuntimeException("HR Approver not found"));
            expense.setHrApprover(hrApprover);
        }

        if (expense.getApprover() != null && expense.getApprover().getUser_id() != null) {
            User approver = userRepository.findById(expense.getApprover().getUser_id())
                    .orElseThrow(() -> new RuntimeException("Approver not found"));
            expense.setApprover(approver);
        }

        return expenseRepository.save(expense);
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public List<Expense> getExpensesByUserId(Long userId) {
        return expenseRepository.findByUser_UserId(userId);
    }

    public List<Expense> getExpensesBySessionId(Long sessionId) {
        return expenseRepository.findBySession_SessionId(sessionId);
    }
    public Map<String, Object> approveRequest(ExpenseApprovalRequest request) {
        // Validate expenseId
        if (request.getExpenseId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Expense ID is required");
        }

        Expense expense = expenseRepository.findById(request.getExpenseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));

        if (request.getPaymentStatus() != null) {
            expense.setPayment_status(request.getPaymentStatus());
            expenseRepository.save(expense);
            return Map.of("message", "Payment status updated successfully");
        }

        if (request.getRole() == null || request.getApproverId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Approver ID and Role are required");
        }

        User approver = userRepository.findById(request.getApproverId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Approver not found"));

        switch (request.getRole().toUpperCase()) {
            case "MANAGER":
                expense.setPreApprover(approver);
                expense.setManagerStatus(request.getStatus());

                break;

            case "HR":
                expense.setHrApprover(approver);
                expense.setHrStatus(request.getStatus());

                break;

            case "FINANCE":
                expense.setApprover(approver);
                expense.setFinanceStatus(request.getStatus());
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role: " + request.getRole());
        }

        expenseRepository.save(expense);

        return Map.of(
                "message", "Expense approved successfully ",
                "expenseId", expense.getExpense_id()

        );
    }

}
