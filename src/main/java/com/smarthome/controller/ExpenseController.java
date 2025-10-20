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
    @GetMapping("/grouped")
    public ResponseEntity<?> getExpensesGrouped(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String range) {

        List<Map<String, Object>> data = expenseService.getExpensesGrouped(userId, range);
        return ResponseEntity.ok(data);
    }


    @GetMapping("/summary")
    public ResponseEntity<?> getSummary() {
        try {
            return ResponseEntity.ok(expenseService.getSummary());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }



}
