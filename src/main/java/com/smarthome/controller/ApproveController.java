package com.smarthome.controller;

import com.smarthome.Service.ExpenseService;
import com.smarthome.dto.ExpenseApprovalRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
public class ApproveController {

    @Autowired
    private ExpenseService expenseService;


    @PutMapping("/approve")
    public ResponseEntity<Map<String, Object>> approveExpense(@RequestBody ExpenseApprovalRequest request) {
        Map<String, Object> response = expenseService.approveRequest(request);
        return ResponseEntity.ok(response);
    }
}
