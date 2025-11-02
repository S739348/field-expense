package com.aarya.fieldemployee.controller;

import com.aarya.fieldemployee.dtorequest.EmployeeRegistrationRequest;
import com.aarya.fieldemployee.dtorequest.EmployeeUpdateRequest;
import com.aarya.fieldemployee.dtorequest.EmployeeDeleteRequest;
import com.aarya.fieldemployee.dtorequest.LoginRequest;
import com.aarya.fieldemployee.dtoresponse.EmployeeResponse;
import com.aarya.fieldemployee.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin("*")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteEmployee(
            @Valid @RequestBody EmployeeDeleteRequest request,
            @RequestHeader(name = "x-user-id", required = true) UUID requestingUserId) {
        employeeService.deleteEmployees(request, requestingUserId);
        return ResponseEntity.ok("Employees deleted successfully");
    }

    @GetMapping("/show")
    public ResponseEntity<List<EmployeeResponse>> showEmployee(@RequestHeader(name = "x-user-id", required = true) UUID requestingUserId) {
        List<EmployeeResponse> response = employeeService.showEmployee(requestingUserId);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/create", consumes = {"multipart/form-data"})
    public ResponseEntity<EmployeeResponse> createEmployee(
            @Valid @ModelAttribute EmployeeRegistrationRequest request,
            @RequestHeader(name = "x-user-id", required = true) UUID requestingUserId) {
        EmployeeResponse response = employeeService.createEmployee(request, requestingUserId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<EmployeeResponse> loginEmployee(@Valid @RequestBody LoginRequest request) {
        EmployeeResponse response = employeeService.loginEmployee(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/update")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @Valid @RequestBody EmployeeUpdateRequest request,
            @RequestHeader(name = "x-user-id", required = true) UUID requestingUserId) {
        EmployeeResponse response = employeeService.updateEmployee(request, requestingUserId);
        return ResponseEntity.ok(response);
    }


}