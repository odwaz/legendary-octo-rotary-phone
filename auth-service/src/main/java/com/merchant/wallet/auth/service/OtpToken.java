package com.merchant.wallet.auth.service;

import java.time.LocalDateTime;

public class OtpToken {
    private String otp;
    private String email;
    private LocalDateTime expiresAt;
    
    public OtpToken(String otp, String email, LocalDateTime expiresAt) {
        this.otp = otp;
        this.email = email;
        this.expiresAt = expiresAt;
    }
    
    public String getOtp() {
        return otp;
    }
    
    public String getEmail() {
        return email;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}