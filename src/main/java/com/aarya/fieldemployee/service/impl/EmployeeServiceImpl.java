package com.aarya.fieldemployee.service.impl;

import com.aarya.fieldemployee.dtorequest.EmployeeDeleteRequest;
import com.aarya.fieldemployee.dtorequest.EmployeeRegistrationRequest;
import com.aarya.fieldemployee.dtorequest.EmployeeUpdateRequest;
import com.aarya.fieldemployee.dtorequest.LoginRequest;
import com.aarya.fieldemployee.dtoresponse.EmployeeResponse;
import com.aarya.fieldemployee.exception.EmailAlreadyExistsException;
import com.aarya.fieldemployee.exception.MobileAlreadyExistsException;
import com.aarya.fieldemployee.exception.UnauthorizedRoleException;
import com.aarya.fieldemployee.model.Employee;
import com.aarya.fieldemployee.repository.EmployeeRepository;
import com.aarya.fieldemployee.service.EmployeeService;
import com.aarya.fieldemployee.service.ImageService;
import com.aarya.fieldemployee.util.Authorized;
import com.aarya.fieldemployee.util.Role;
import com.aarya.fieldemployee.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ImageService imageService;
    @Autowired
    Authorized authorized;

    @Override
    @Transactional
    public EmployeeResponse createEmployee(EmployeeRegistrationRequest request, UUID requestingUserId) {

        authorized.validateAuthorization(requestingUserId);
        validateRegistrationRequest(request);

        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered: " + request.getEmail());
        }
        if (employeeRepository.existsByMobile(request.getMobile())) {
            throw new MobileAlreadyExistsException("Mobile number already registered: " + request.getMobile());
        }

        Employee employee = new Employee();
        employee.setName(request.getName());
        employee.setEmail(request.getEmail());
        employee.setMobile(request.getMobile());
        employee.setPassword(request.getPassword());

        if (request.getProfileImage() != null && !request.getProfileImage().isEmpty()) {
            try {

                String fileName = imageService.saveProfileImage(request.getProfileImage());
                employee.setProfileImage(fileName);
            } catch (IOException e) {
                throw new RuntimeException("Failed to process profile image", e);
            }
        }

        if (request.getRole() != null) {
            employee.setRole(request.getRole());
        }

        if (request.getManagerId() != null) {
            employeeRepository.findById(request.getManagerId())
                    .ifPresent(employee::setManager);
        }
        employee = employeeRepository.save(employee);
        return convertToResponse(employee);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(EmployeeUpdateRequest request, UUID requestingUserId) {
        authorized.validateAuthorization(requestingUserId);

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (request.getName() != null) employee.setName(request.getName());
        if (request.getMobile() != null) {
            if (employeeRepository.existsByMobileAndEmployeeIdNot(request.getMobile(), request.getEmployeeId())) {
                throw new MobileAlreadyExistsException("Mobile number already registered");
            }
            if (request.getMobile() != null && !Validator.isValidIndianMobile(String.valueOf(request.getMobile()))) {
                throw new IllegalArgumentException("Invalid mobile number format");
            }
            employee.setMobile(request.getMobile());
        }
        if (request.getEmail() != null) {
            if (employeeRepository.existsByEmailAndEmployeeIdNot(request.getEmail(), request.getEmployeeId())) {
                throw new EmailAlreadyExistsException("Email already registered");
            }
            if (request.getEmail() != null && !Validator.isValidEmail(request.getEmail())) {
                throw new IllegalArgumentException("Invalid email format");
            }
            employee.setEmail(request.getEmail());
        }

        if (request.getPassword() != null && !Validator.isValidPassword(request.getPassword())) {
            throw new IllegalArgumentException(Validator.getPasswordRequirements());
        }
        employee.setPassword(request.getPassword());
        if (request.getRole() != null) employee.setRole(request.getRole());
        if (request.getStatus() != null) employee.setStatus(request.getStatus());
        if (request.getManagerId() != null) {
            employeeRepository.findById(request.getManagerId())
                    .ifPresent(employee::setManager);
        }
        employee = employeeRepository.save(employee);
        return convertToResponse(employee);
    }

    @Override
    @Transactional
    public void deleteEmployees(EmployeeDeleteRequest request, UUID requestingUserId) {
        authorized.validateAuthorization(requestingUserId);
        employeeRepository.deleteAllById(request.getEmployeeIds());
    }

    @Override
    @Transactional
    public List<EmployeeResponse> showEmployee(UUID requestingUserId) {
        Optional<Employee> requestedUser = employeeRepository.findById(requestingUserId);
        Employee requestedEmployee = requestedUser.get();
        String role = Role.getRole(requestedEmployee.getRole());
        List<Employee> employees;

        switch (role) {
            case "Field_Employee_Full_Time":
            case "Field_Employee_Vendor":
                employees = List.of(requestedEmployee);
                return employees.stream().map(this::convertToResponse).toList();
            case "Manager":
                employees = requestedEmployee.getSubordinates();
                return employees.stream().map(this::convertToResponseForManager).toList();
            case "Hr":
            case "Finance":
                employees = employeeRepository.findAll();
                return employees.stream().map(this::convertToResponse).toList();
            default:
                throw new IllegalArgumentException("Invalid or unauthorized role for this user");
        }
    }
    @Override
    public EmployeeResponse loginEmployee(LoginRequest request) {
        Employee employee = null;

        // Check if username is email or mobile
        if (Validator.isValidEmail(request.getUsername())) {
            employee = employeeRepository.findByEmailAndPassword(request.getUsername(), request.getPassword())
                    .orElse(null);
        } else {
            try {
                Long mobile = Long.parseLong(request.getUsername());
                employee = employeeRepository.findByMobileAndPassword(mobile, request.getPassword())
                        .orElse(null);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Invalid username format");
            }
        }

        if (employee == null) {
            throw new RuntimeException("Invalid credentials");
        }

        if (employee.getStatus() != Employee.Status.Active) {
            throw new RuntimeException("Account is inactive");
        }
        return convertToResponse(employee);
    }


    private void validateRegistrationRequest(EmployeeRegistrationRequest request) {
        if (!Validator.isValidIndianMobile(String.valueOf(request.getMobile()))) {
            throw new IllegalArgumentException("Invalid mobile number format");
        }

        if (!Validator.isValidEmail(request.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (!Validator.isValidPassword(request.getPassword())) {
            throw new IllegalArgumentException(Validator.getPasswordRequirements());
        }
    }

    private EmployeeResponse convertToResponse(Employee employee) {
        EmployeeResponse response = new EmployeeResponse();
        response.setEmployeeId(employee.getEmployeeId());
        response.setName(employee.getName());
        response.setEmail(employee.getEmail());
        response.setMobile(employee.getMobile());
        response.setProfileUrl(employee.getProfileImage());
        response.setRole(employee.getRole());
        response.setStatus(employee.getStatus());
        if (employee.getManager() != null) {
            response.setManagerId(employee.getManager().getEmployeeId());
            response.setManagerName(employee.getManager().getName());
        }
        return response;
    }

    private EmployeeResponse convertToResponseForManager(Employee employee) {
        EmployeeResponse response = new EmployeeResponse();
        response.setEmployeeId(employee.getEmployeeId());
        response.setName(employee.getName());
        response.setEmail(employee.getEmail());
        response.setMobile(employee.getMobile());
        response.setProfileUrl(employee.getProfileImage());
        response.setRole(employee.getRole());
        response.setStatus(employee.getStatus());
        return response;
    }

}