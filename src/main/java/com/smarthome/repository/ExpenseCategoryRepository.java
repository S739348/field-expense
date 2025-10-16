package com.smarthome.repository;

import com.smarthome.model.ExpenseCategories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategories, Long> {
    boolean existsByName(String name);
}
