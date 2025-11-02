package com.aarya.fieldemployee.controller;

import com.aarya.fieldemployee.dtorequest.ExpenseCreateRequest;
import com.aarya.fieldemployee.dtorequest.ExpenseUpdateRequest;
import com.aarya.fieldemployee.dtorequest.ExpenseDeleteRequest;
import com.aarya.fieldemployee.dtoresponse.ExpenseResponse;
import com.aarya.fieldemployee.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    public ResponseEntity<ExpenseResponse> createExpense(
            @Valid @ModelAttribute ExpenseCreateRequest request,
            @RequestHeader(name = "x-user-id", required = true) UUID requestingUserId) {
        ExpenseResponse response = expenseService.createExpense(request, requestingUserId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(value = "/update", consumes = {"multipart/form-data"})
    public ResponseEntity<ExpenseResponse> updateExpense(
            @Valid @ModelAttribute ExpenseUpdateRequest request,
            @RequestHeader(name = "x-user-id", required = true) UUID requestingUserId) {
        ExpenseResponse response = expenseService.updateExpense(request, requestingUserId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteExpenses(
            @Valid @RequestBody ExpenseDeleteRequest request,
            @RequestHeader(name = "x-user-id", required = true) UUID requestingUserId) {
        expenseService.deleteExpenses(request, requestingUserId);
        return ResponseEntity.ok("Expenses deleted successfully");
    }

    @GetMapping("/show")
    public ResponseEntity<List<ExpenseResponse>> showExpenses(
            @RequestHeader(name = "x-user-id", required = true) UUID requestingUserId,
            @RequestParam(name = "range", required = false) String range) {
        List<ExpenseResponse> response = expenseService.showExpenses(requestingUserId, range);
        return ResponseEntity.ok(response);
    }
}
