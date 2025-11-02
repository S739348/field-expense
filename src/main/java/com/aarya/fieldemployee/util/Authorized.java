package com.aarya.fieldemployee.util;

import com.aarya.fieldemployee.exception.UnauthorizedRoleException;
import com.aarya.fieldemployee.model.Employee;
import com.aarya.fieldemployee.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;
@Component
public class Authorized {

    @Autowired
    private EmployeeRepository employeeRepository;

    public void validateAuthorization(UUID requestingUserId) {
        Employee requestingEmployee = employeeRepository.findById(requestingUserId)
                .orElseThrow(() -> new UnauthorizedRoleException("Invalid requesting user ID"));

        boolean isAuthorized = (((requestingEmployee.getRole() == Employee.Role.Admin)||(requestingEmployee.getRole() == Employee.Role.Hr)) && (requestingEmployee.getStatus().equals(Employee.Status.Active)));

        if (!isAuthorized) {
            throw new UnauthorizedRoleException(
                    "Only Admin and Hr roles can create new employees. Your role: " +
                            requestingEmployee.getRole());
        }
    }

    public void validateManager(Employee  requestingEmployee) {

        boolean isAuthorized = (((requestingEmployee.getRole() == Employee.Role.Manager)||(requestingEmployee.getRole() == Employee.Role.Admin)) && (requestingEmployee.getStatus().equals(Employee.Status.Active)));

        if (!isAuthorized) {
            throw new UnauthorizedRoleException(
                    "Only an active Manager & Admin can assign tasks. Your role: " +
                            requestingEmployee.getRole()+ ", Status: " + requestingEmployee.getStatus());
        }
    }
    public void validateEmplyoee(Employee requestingEmployee) {

        boolean isAuthorized = (((requestingEmployee.getRole() == Employee.Role.Field_Employee_Vendor)||(requestingEmployee.getRole() == Employee.Role.Field_Employee_Full_Time)) && (requestingEmployee.getStatus().equals(Employee.Status.Active)));

        if (!isAuthorized) {
            throw new UnauthorizedRoleException(
                    "Task can only be assigned to active Field Employees." +
                            requestingEmployee.getRole()+ ", Status: " + requestingEmployee.getStatus());
        }
    }
    public void validateIsAdmin(Employee requestingEmployee) {

        boolean isAuthorized = ((requestingEmployee.getRole() == Employee.Role.Admin) && (requestingEmployee.getStatus().equals(Employee.Status.Active)));

        if (!isAuthorized) {
            throw new UnauthorizedRoleException(
                    "Admin Can update that thing." +
                            requestingEmployee.getRole()+ ", Status: " + requestingEmployee.getStatus());
        }
    }


}
