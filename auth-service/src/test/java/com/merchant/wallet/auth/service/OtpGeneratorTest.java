package com.merchant.wallet.auth.service;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class OtpGeneratorTest {

    @Test
    void shouldGenerateSixDigitOtp() {
        OtpGenerator generator = new OtpGenerator();
        
        String otp = generator.generateOtp();
        
        assertNotNull(otp);
        assertEquals(6, otp.length());
        assertTrue(otp.matches("\\d{6}"));
    }
    
    @Test
    void shouldCreateOtpWithExpiration() {
        OtpGenerator generator = new OtpGenerator();
        
        OtpToken token = generator.generateOtpToken("user@example.com");
        
        assertNotNull(token);
        assertNotNull(token.getOtp());
        assertEquals(6, token.getOtp().length());
        assertEquals("user@example.com", token.getEmail());
        assertNotNull(token.getExpiresAt());
        assertTrue(token.getExpiresAt().isAfter(LocalDateTime.now()));
    }
    
    @Test
    void shouldValidateNonExpiredOtp() {
        OtpGenerator generator = new OtpGenerator();
        OtpToken token = generator.generateOtpToken("user@example.com");
        
        boolean isValid = generator.isValidOtp(token.getEmail(), token.getOtp());
        
        assertTrue(isValid);
    }
    
    @Test
    void shouldRejectExpiredOtp() {
        OtpGenerator generator = new OtpGenerator();
        OtpToken expiredToken = new OtpToken("123456", "user@example.com", LocalDateTime.now().minusMinutes(10));
        generator.storeToken(expiredToken);
        
        boolean isValid = generator.isValidOtp("user@example.com", "123456");
        
        assertFalse(isValid);
    }
    
    @Test
    void shouldRejectWrongOtp() {
        OtpGenerator generator = new OtpGenerator();
        generator.generateOtpToken("user@example.com");
        
        boolean isValid = generator.isValidOtp("user@example.com", "999999");
        
        assertFalse(isValid);
    }
    
    @Test
    void shouldRejectNonExistentEmail() {
        OtpGenerator generator = new OtpGenerator();
        
        boolean isValid = generator.isValidOtp("nonexistent@example.com", "123456");
        
        assertFalse(isValid);
    }
}