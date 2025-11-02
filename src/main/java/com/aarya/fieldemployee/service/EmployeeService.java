package com.aarya.fieldemployee.service;

import com.aarya.fieldemployee.dtorequest.EmployeeDeleteRequest;
import com.aarya.fieldemployee.dtorequest.EmployeeRegistrationRequest;
import com.aarya.fieldemployee.dtorequest.EmployeeUpdateRequest;
import com.aarya.fieldemployee.dtorequest.LoginRequest;
import com.aarya.fieldemployee.dtoresponse.EmployeeResponse;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {
    EmployeeResponse createEmployee(EmployeeRegistrationRequest request, UUID requestingUserId);
    EmployeeResponse updateEmployee(EmployeeUpdateRequest request, UUID requestingUserId);
    void deleteEmployees(EmployeeDeleteRequest request, UUID requestingUserId);
    List<EmployeeResponse> showEmployee(UUID requestingUserId);
    EmployeeResponse loginEmployee(LoginRequest request);

}