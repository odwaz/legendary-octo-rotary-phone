package com.merchant.wallet.auth.service;

public class UserRegistrationValidator {
    
    public boolean isValidEmail(String email) {
        return email != null && email.contains("@");
    }
    
    public boolean validateRequiredFields(UserRegistrationRequest request) {
        return request.getEmail() != null && !request.getEmail().trim().isEmpty() &&
               request.getFullName() != null && !request.getFullName().trim().isEmpty() &&
               request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty();
    }
}