package com.smarthome.controller;

import com.smarthome.Service.ExpenseService;
import com.smarthome.dto.ExpenseApprovalRequest;
import com.smarthome.model.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping("/create")
    public ResponseEntity<?> createExpense(@RequestBody Expense expense) {
        if (expense == null || expense.getAmount() == null) {
            return ResponseEntity.badRequest().body("Invalid expense data");
        }

        Expense created = expenseService.createExpense(expense);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping
    public ResponseEntity<?> getExpenses(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long sessionId) {

        List<Expense> expenses;

        if (userId != null) {
            expenses = expenseService.getExpensesByUserId(userId);
            if (expenses.isEmpty()) {
                return ResponseEntity.status(404).body("No expenses found for user ID: " + userId);
            }
        } else if (sessionId != null) {
            expenses = expenseService.getExpensesBySessionId(sessionId);
            if (expenses.isEmpty()) {
                return ResponseEntity.status(404).body("No expenses found for session ID: " + sessionId);
            }
        } else {
            expenses = expenseService.getAllExpenses();
        }

        return ResponseEntity.ok(expenses);
    }

    @PostMapping("/approve")
    public ResponseEntity<?> approveRequest(@RequestBody ExpenseApprovalRequest request) {
        try {
            if (request == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid data"));
            }

            Map<String, Object> result = expenseService.approveRequest(request);
            return ResponseEntity.ok(result);

        } catch (ResponseStatusException ex) {
            // Handles errors thrown by service (e.g., NOT_FOUND, BAD_REQUEST)
            return ResponseEntity
                    .status(ex.getStatusCode())
                    .body(Map.of("error", ex.getReason()));

        } catch (Exception e) {
            // Handles any unexpected runtime errors
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Something went wrong: " + e.getMessage()));
        }
    }



}
