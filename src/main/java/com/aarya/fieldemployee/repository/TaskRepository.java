package com.aarya.fieldemployee.repository;

import com.aarya.fieldemployee.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByEmployeeEmployeeId(UUID employeeId);
    List<Task> findByManagerEmployeeId(UUID managerId);

    List<Task> findByEmployeeEmployeeIdAndCreatedAtBetween(UUID employeeId, LocalDateTime startDate, LocalDateTime endDate);
    List<Task> findByManagerEmployeeIdAndCreatedAtBetween(UUID managerId, LocalDateTime startDate, LocalDateTime endDate);
    List<Task> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
