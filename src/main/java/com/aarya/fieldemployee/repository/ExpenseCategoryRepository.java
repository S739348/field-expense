package com.aarya.fieldemployee.repository;

import com.aarya.fieldemployee.model.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {
    boolean existsByName(String name);
    List<ExpenseCategory> findAllByOrderByNameAsc();

    @Query("SELECT COUNT(e) > 0 FROM Expense e WHERE e.category.id = :categoryId")
    boolean hasAssociatedExpenses(@Param("categoryId") Long categoryId);
}
