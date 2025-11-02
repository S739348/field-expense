package com.aarya.fieldemployee.util;

import com.aarya.fieldemployee.exception.UnauthorizedRoleException;
import com.aarya.fieldemployee.model.Employee;

import java.util.UUID;
import java.util.regex.Pattern;

public class Validator {
    // Regex pattern for Indian mobile numbers (10 digits, optionally prefixed with +91)
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^(?:(?:\\+|0{0,2})91[\\s-]*)?[6789]\\d{9}$");
    
    // Email pattern as per RFC 5322 standards
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
    
    // Password pattern: minimum 8 chars, max 20 chars, at least one uppercase, one lowercase, one number, one special char
    private static final Pattern PASSWORD_PATTERN = 
        Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$");

    /**
     * Validates an Indian mobile number.
     * Valid formats: 
     * - 10 digits starting with 6,7,8,9 (e.g., 9876543210)
     * - With country code +91 (e.g., +919876543210)
     * 
     * @param mobileNumber The mobile number to validate
     * @return true if the mobile number is valid, false otherwise
     */
    public static boolean isValidIndianMobile(String mobileNumber) {
        if (mobileNumber == null || mobileNumber.trim().isEmpty()) {
            return false;
        }
        return MOBILE_PATTERN.matcher(mobileNumber).matches();
    }

    /**
     * Validates an email address according to RFC 5322 standards.
     * 
     * @param email The email address to validate
     * @return true if the email is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validates a password against the following rules:
     * - Minimum 8 characters
     * - Maximum 20 characters
     * - At least one uppercase letter
     * - At least one lowercase letter
     * - At least one number
     * - At least one special character (@$!%*?&)
     * 
     * @param password The password to validate
     * @return true if the password meets all requirements, false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Returns a descriptive message about password requirements.
     * 
     * @return String containing password requirements
     */
    public static String getPasswordRequirements() {
        return "Password must contain:\n" +
               "- 8 to 20 characters\n" +
               "- At least one uppercase letter\n" +
               "- At least one lowercase letter\n" +
               "- At least one number\n" +
               "- At least one special character (@$!%*?&)";
    }



}