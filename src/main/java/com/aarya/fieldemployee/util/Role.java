package com.aarya.fieldemployee.util;

import com.aarya.fieldemployee.model.Employee;

public class Role {

    public static String  getRole(Employee.Role role){
        if(role.equals(Employee.Role.Field_Employee_Full_Time))
            return "Field_Employee_Full_Time";
        else if(role.equals(Employee.Role.Field_Employee_Vendor))
            return "Field_Employee_Vendor";
        else if(role.equals(Employee.Role.Manager))
           return "Manager";
        else if (role.equals(Employee.Role.Hr))
            return "Hr";
        else if(role.equals(Employee.Role.Finance))
            return "Finance";
        else
            return "Other";
    }

}
