package com.merchant.wallet.webhook.util;

import java.math.BigDecimal;
import java.util.Map;

public class RequestValidator {

    public static void validateDepositRequest(Map<String, Object> request) {
        if (!request.containsKey("walletId")) {
            throw new IllegalArgumentException("Invalid request: missing walletId");
        }
        if (!request.containsKey("amount")) {
            throw new IllegalArgumentException("Invalid request: missing amount");
        }
        
        try {
            Long.valueOf(request.get("walletId").toString());
            new BigDecimal(request.get("amount").toString());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid request: invalid data format");
        }
    }

    public static void validateWithdrawalRequest(Map<String, Object> request) {
        if (!request.containsKey("walletId")) {
            throw new IllegalArgumentException("Invalid request: missing walletId");
        }
        if (!request.containsKey("amount")) {
            throw new IllegalArgumentException("Invalid request: missing amount");
        }
        
        try {
            Long.valueOf(request.get("walletId").toString());
            new BigDecimal(request.get("amount").toString());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid request: invalid data format");
        }
    }

    public static void validateTransferRequest(Map<String, Object> request) {
        if (!request.containsKey("fromWalletId")) {
            throw new IllegalArgumentException("Invalid request: missing fromWalletId");
        }
        if (!request.containsKey("toWalletId")) {
            throw new IllegalArgumentException("Invalid request: missing toWalletId");
        }
        if (!request.containsKey("amount")) {
            throw new IllegalArgumentException("Invalid request: missing amount");
        }
        
        try {
            Long.valueOf(request.get("fromWalletId").toString());
            Long.valueOf(request.get("toWalletId").toString());
            new BigDecimal(request.get("amount").toString());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid request: invalid data format");
        }
    }
}