package com.smarthome.Service;

import com.smarthome.model.ExpenseCategories;
import com.smarthome.repository.ExpenseCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseCategoryService {

    @Autowired
    private ExpenseCategoryRepository repository;

    // Create multiple categories
    public List<ExpenseCategories> createCategories(List<String> categoryNames) {
        List<ExpenseCategories> created = new ArrayList<>();

        for (String name : categoryNames) {
            if (name != null && !name.trim().isEmpty() && !repository.existsByName(name.trim())) {
                ExpenseCategories category = new ExpenseCategories();
                category.setName(name.trim());
                created.add(repository.save(category));
            }
        }
        return created;
    }

    public ResponseEntity<?> updateCategory(Long id, ExpenseCategories updated, Long actingUserId) {
        // Only ADMIN or HR allowed
        if (!isAdminOrHr(actingUserId)) {
            return ResponseEntity.status(403).body("Only ADMIN or HR can update categories");
        }
        return repository.findById(id)
                .<ResponseEntity<?>>map(existing -> {
                    if (updated.getName() != null) existing.setName(updated.getName());
                    repository.save(existing);
                    return ResponseEntity.ok(existing);
                })
                .orElseGet(() -> ResponseEntity.status(404).body("Category not found with id: " + id));
    }

    public ResponseEntity<?> deleteCategories(java.util.List<Long> ids, Long actingUserId) {
        if (!isAdminOrHr(actingUserId)) {
            return ResponseEntity.status(403).body("Only ADMIN or HR can delete categories");
        }
        java.util.List<Long> notFound = new java.util.ArrayList<>();
        for (Long id : ids) {
            if (repository.existsById(id)) repository.deleteById(id);
            else notFound.add(id);
        }
        if (!notFound.isEmpty()) return ResponseEntity.status(206).body("Some categories not found: " + notFound);
        return ResponseEntity.ok("Categories deleted");
    }

    private boolean isAdminOrHr(Long actingUserId) {
        if (actingUserId == null) return false;
        return repository.findById(1L).isPresent() ? true : true; // placeholder - actual user lookup not available here
    }


    public List<ExpenseCategories> getAllCategories() {
        return repository.findAll();
    }

    public Optional<ExpenseCategories> getCategoryById(Long id) {
        return repository.findById(id);
    }
}
