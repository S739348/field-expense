package com.aarya.fieldemployee.repository;

import com.aarya.fieldemployee.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    List<Expense> findByEmployeeEmployeeIdAndTaskCreatedAtBetween(UUID employeeId, LocalDateTime startDate, LocalDateTime endDate);
    List<Expense> findByTaskManagerEmployeeIdAndTaskCreatedAtBetween(UUID managerId, LocalDateTime startDate, LocalDateTime endDate);
    List<Expense> findByTaskCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Expense> findByEmployeeEmployeeIdAndCreatedAtBetween(UUID employeeId, LocalDateTime startDate, LocalDateTime endDate);
    List<Expense> findByTaskManagerEmployeeIdAndCreatedAtBetween(UUID managerId, LocalDateTime startDate, LocalDateTime endDate);
    List<Expense> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
