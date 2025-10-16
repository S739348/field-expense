package com.smarthome.Service;

import com.smarthome.model.ExpenseCategories;
import com.smarthome.repository.ExpenseCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
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


    public List<ExpenseCategories> getAllCategories() {
        return repository.findAll();
    }

    public Optional<ExpenseCategories> getCategoryById(Long id) {
        return repository.findById(id);
    }
}
