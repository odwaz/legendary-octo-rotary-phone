package com.merchant.wallet.auth.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserRegistrationValidatorTest {

    @Test
    void shouldValidateValidEmail() {
        UserRegistrationValidator validator = new UserRegistrationValidator();
        
        boolean isValid = validator.isValidEmail("user@example.com");
        
        assertTrue(isValid);
    }
    
    @Test
    void shouldRejectInvalidEmail() {
        UserRegistrationValidator validator = new UserRegistrationValidator();
        
        boolean isValid = validator.isValidEmail("invalid-email");
        
        assertFalse(isValid);
    }
    
    @Test
    void shouldRejectNullEmail() {
        UserRegistrationValidator validator = new UserRegistrationValidator();
        
        boolean isValid = validator.isValidEmail(null);
        
        assertFalse(isValid);
    }
    
    @Test
    void shouldValidateRequiredFields() {
        UserRegistrationValidator validator = new UserRegistrationValidator();
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("user@example.com");
        request.setFullName("John Doe");
        request.setPhoneNumber("+27123456789");
        
        boolean isValid = validator.validateRequiredFields(request);
        
        assertTrue(isValid);
    }
    
    @Test
    void shouldRejectMissingEmail() {
        UserRegistrationValidator validator = new UserRegistrationValidator();
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setFullName("John Doe");
        request.setPhoneNumber("+27123456789");
        
        boolean isValid = validator.validateRequiredFields(request);
        
        assertFalse(isValid);
    }
    
    @Test
    void shouldRejectEmptyFullName() {
        UserRegistrationValidator validator = new UserRegistrationValidator();
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail("user@example.com");
        request.setFullName("");
        request.setPhoneNumber("+27123456789");
        
        boolean isValid = validator.validateRequiredFields(request);
        
        assertFalse(isValid);
    }
}