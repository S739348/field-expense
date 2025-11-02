package com.aarya.fieldemployee.repository;

import com.aarya.fieldemployee.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    boolean existsByEmail(String email);
    boolean existsByMobile(Long mobile);
    boolean existsByMobileAndEmployeeIdNot(Long mobile, UUID employeeId);
    boolean existsByEmailAndEmployeeIdNot(String email, UUID employeeId);
    //List<Employee> findByManagerEmployeeId(UUID managerId);
    Optional<Employee> findByEmailAndPassword(String email, String password);
    Optional<Employee> findByMobileAndPassword(Long mobile, String password);

}