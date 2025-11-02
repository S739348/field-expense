package com.aarya.fieldemployee.controller;

import com.aarya.fieldemployee.dtorequest.ExpenseCategoryCreateRequest;
import com.aarya.fieldemployee.dtorequest.ExpenseCategoryDeleteRequest;
import com.aarya.fieldemployee.dtoresponse.ExpenseCategoryResponse;
import com.aarya.fieldemployee.service.ExpenseCategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/expense-categories")
public class ExpenseCategoryController {

    @Autowired
    private ExpenseCategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<ExpenseCategoryResponse> createCategory(
            @Valid @RequestBody ExpenseCategoryCreateRequest request,
            @RequestHeader(name = "x-user-id", required = true) UUID requestingUserId) {
        ExpenseCategoryResponse response = categoryService.createCategory(request, requestingUserId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteCategories(
            @Valid @RequestBody ExpenseCategoryDeleteRequest request,
            @RequestHeader(name = "x-user-id", required = true) UUID requestingUserId) {
        categoryService.deleteCategories(request, requestingUserId);
        return ResponseEntity.ok("Categories deleted successfully");
    }

    @GetMapping("/show")
    public ResponseEntity<List<ExpenseCategoryResponse>> showCategories() {
        List<ExpenseCategoryResponse> response = categoryService.showCategories();
        return ResponseEntity.ok(response);
    }
}
