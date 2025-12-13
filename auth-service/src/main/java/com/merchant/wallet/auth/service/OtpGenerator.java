package com.merchant.wallet.auth.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class OtpGenerator {
    private final Random random = new Random();
    private final Map<String, OtpToken> tokenStore = new ConcurrentHashMap<>();
    
    public String generateOtp() {
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
    
    public OtpToken generateOtpToken(String email) {
        String otp = generateOtp();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);
        OtpToken token = new OtpToken(otp, email, expiresAt);
        tokenStore.put(email, token);
        return token;
    }
    
    public boolean isValidOtp(String email, String otp) {
        OtpToken token = tokenStore.get(email);
        if (token == null) {
            return false;
        }
        return !token.isExpired() && token.getOtp().equals(otp);
    }
    
    public void storeToken(OtpToken token) {
        tokenStore.put(token.getEmail(), token);
    }
}