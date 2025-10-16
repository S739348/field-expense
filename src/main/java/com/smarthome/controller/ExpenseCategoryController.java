package com.smarthome.controller;

import com.smarthome.model.ExpenseCategories;
import com.smarthome.Service.ExpenseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class ExpenseCategoryController {

    @Autowired
    private ExpenseCategoryService service;

    @PostMapping("/create")
    public ResponseEntity<?> createCategories(@RequestBody List<String> names) {
        if (names == null || names.isEmpty()) {
            return ResponseEntity.status(400).body("No category names provided");
        }

        List<ExpenseCategories> created = service.createCategories(names);
        if (created.isEmpty()) {
            return ResponseEntity.status(204).body("No new categories added (duplicates or invalid)");
        }
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ExpenseCategories>> getAllCategories() {
        return ResponseEntity.ok(service.getAllCategories());
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        return service.getCategoryById(id)
                .<ResponseEntity<?>>map(category -> ResponseEntity.ok(category))
                .orElseGet(() -> ResponseEntity.status(404).body("Category not found with id: " + id));
    }


}
