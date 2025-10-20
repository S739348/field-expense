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
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

    /**
     * Create Expense safely using IDs from JSON.
     */
    public Expense createExpense(Expense expense) {
        // ✅ Fetch entities using IDs (prevent NPE)
        if (expense.getUser() == null || expense.getUser().getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
        }
        User user = userRepository.findById(expense.getUser().getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        expense.setUser(user);

        if (expense.getSession() == null || expense.getSession().getSessionId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session ID is required");
        }
        Session session = sessionRepository.findById(expense.getSession().getSessionId())
                .orElseThrow(() -> new RuntimeException("Session not found"));
        expense.setSession(session);

        if (expense.getCategory() == null || expense.getCategory().getCategory_id() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category ID is required");
        }
        ExpenseCategories category = expenseCategoryRepository.findById(expense.getCategory().getCategory_id())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        expense.setCategory(category);

        // Optional approvers
        if (expense.getPreApprover() != null && expense.getPreApprover().getUserId() != null) {
            User preApprover = userRepository.findById(expense.getPreApprover().getUserId())
                    .orElseThrow(() -> new RuntimeException("PreApprover not found"));
            expense.setPreApprover(preApprover);
        }

        if (expense.getHrApprover() != null && expense.getHrApprover().getUserId() != null) {
            User hrApprover = userRepository.findById(expense.getHrApprover().getUserId())
                    .orElseThrow(() -> new RuntimeException("HR Approver not found"));
            expense.setHrApprover(hrApprover);
        }

        if (expense.getApprover() != null && expense.getApprover().getUserId() != null) {
            User approver = userRepository.findById(expense.getApprover().getUserId())
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

    public Map<String, Object> getSummary() {
        List<Expense> all = expenseRepository.findAll();
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        java.math.BigDecimal approvedAmount = java.math.BigDecimal.ZERO;
        java.math.BigDecimal rejectedAmount = java.math.BigDecimal.ZERO;
        java.math.BigDecimal unpaidAmount = java.math.BigDecimal.ZERO;
        long pending = 0;

        for (Expense e : all) {
            if (e.getAmount() != null) total = total.add(e.getAmount());
            if ("Approved".equalsIgnoreCase(e.getFinanceStatus()) ||
                    "Approved".equalsIgnoreCase(e.getHrStatus()) ||
                    "Approved".equalsIgnoreCase(e.getManagerStatus())) {
                if (e.getAmount() != null) approvedAmount = approvedAmount.add(e.getAmount());
            }
            if ("Rejected".equalsIgnoreCase(e.getFinanceStatus()) ||
                    "Rejected".equalsIgnoreCase(e.getHrStatus()) ||
                    "Rejected".equalsIgnoreCase(e.getManagerStatus())) {
                if (e.getAmount() != null) rejectedAmount = rejectedAmount.add(e.getAmount());
            }
            if (e.getPayment_status() != null && e.getPayment_status() != Expense.PaymentStatus.paid) {
                if (e.getAmount() != null) unpaidAmount = unpaidAmount.add(e.getAmount());
            }
            if ((e.getManagerStatus() == null || e.getManagerStatus().isEmpty() || "Pending".equalsIgnoreCase(e.getManagerStatus())) ||
                    (e.getHrStatus() == null || e.getHrStatus().isEmpty() || "Pending".equalsIgnoreCase(e.getHrStatus())) ||
                    (e.getFinanceStatus() == null || e.getFinanceStatus().isEmpty() || "Pending".equalsIgnoreCase(e.getFinanceStatus()))) {
                pending++;
            }
        }

        return Map.of(
                "totalExpenses", total,
                "approvalApproved", approvedAmount,
                "approvalRejected", rejectedAmount,
                "unpaidAmount", unpaidAmount,
                "pendingApprovals", pending
        );
    }

    public Map<String, Object> approveRequest(ExpenseApprovalRequest request) {
        if (request.getExpenseId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Expense ID is required");
        }

        Expense expense = expenseRepository.findById(request.getExpenseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));

        if (request.getPaymentStatus() != null) {
            User approverForPayment = userRepository.findById(request.getApproverId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Approver not found"));
            if (approverForPayment.getRole() != User.Role.FINANCE) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only FINANCE can update payment status");
            }
            if (expense.getPayment_status() == Expense.PaymentStatus.paid &&
                    request.getPaymentStatus() != Expense.PaymentStatus.paid) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot revert payment status from PAID");
            }
            expense.setPayment_status(request.getPaymentStatus());
            expenseRepository.save(expense);
            return Map.of("message", "Payment status updated successfully");
        }

        if (request.getRole() == null || request.getApproverId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Approver ID and Role are required");
        }

        User approver = userRepository.findById(request.getApproverId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Approver not found"));

        String role = request.getRole().toUpperCase();
        if (!approver.getRole().name().equalsIgnoreCase(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Approver does not have role: " + role);
        }

        switch (role) {
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
                "message", "Expense approved successfully",
                "expenseId", expense.getExpense_id()
        );
    }
    public List<Map<String, Object>> getExpensesGrouped(Long userId, String range) {
        List<Expense> expenses = getAllExpenses();

        if (userId != null) {
            expenses.removeIf(e -> e.getUser() == null || !e.getUser().getUserId().equals(userId));
        }

        LocalDate start = null, end = null;
        try {
            if (range != null && !range.isBlank()) {
                String[] parts = range.split("-");
                if (parts.length >= 2) {
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    start = LocalDate.parse(parts[0].trim(), fmt);
                    end = LocalDate.parse(parts[1].trim(), fmt);
                }
            }
        } catch (Exception ignored) {}

        if (start == null || end == null) {
            end = LocalDate.now();
            start = end.minusMonths(1).plusDays(1);
        }

        LocalDate finalStart = start, finalEnd = end;
        expenses.removeIf(e -> e.getCreatedAt() == null
                || e.getCreatedAt().toLocalDate().isBefore(finalStart)
                || e.getCreatedAt().toLocalDate().isAfter(finalEnd));

        Map<Long, List<Expense>> grouped = expenses.stream()
                .filter(e -> e.getSession() != null)
                .collect(Collectors.groupingBy(e -> e.getSession().getSessionId()));

        List<Map<String, Object>> result = new ArrayList<>();

        for (var entry : grouped.entrySet()) {
            Session s = expenses.stream()
                    .filter(e -> e.getSession().getSessionId().equals(entry.getKey()))
                    .findFirst()
                    .map(Expense::getSession)
                    .orElse(null);

            if (s == null) continue;

            Map<String, Object> sessionMap = new LinkedHashMap<>();
            sessionMap.put("sessionId", s.getSessionId());
            sessionMap.put("sessionName", s.getSession_name());
            sessionMap.put("startTime", s.getStart_time());
            sessionMap.put("endTime", s.getEnd_time());
            sessionMap.put("totalDistance", s.getTotal_distance());
            sessionMap.put("status", s.getSession_status());

            // ✅ Add user details
            if (s.getUser() != null) {
                Map<String, Object> userMap = new LinkedHashMap<>();
                userMap.put("userId", s.getUser().getUserId());
                userMap.put("name", s.getUser().getName());
                userMap.put("email", s.getUser().getEmail());
                userMap.put("role", s.getUser().getRole());
                sessionMap.put("user", userMap);
            } else {
                sessionMap.put("user", null);
            }

            List<Map<String, Object>> expList = entry.getValue().stream().map(e -> {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("expenseId", e.getExpense_id());
                map.put("category", e.getCategory() != null ? e.getCategory().getName() : null);
                map.put("amount", e.getAmount());
                map.put("description", e.getDescription());
                map.put("createdAt", e.getCreatedAt());
                map.put("managerStatus", e.getManagerStatus());
                map.put("hrStatus", e.getHrStatus());
                map.put("financeStatus", e.getFinanceStatus());
                map.put("paymentStatus", e.getPayment_status() != null ? e.getPayment_status().toString() : null);
                return map;
            }).toList();

            sessionMap.put("expenses", expList);
            result.add(sessionMap);
        }

        return result;
    }

}
