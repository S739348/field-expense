package com.smarthome.repository;

import com.smarthome.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser_UserId(Long userId);
    List<Expense> findBySession_SessionId(Long sessionId);
}
