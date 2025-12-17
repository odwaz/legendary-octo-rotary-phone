package com.merchant.wallet.webhook.util;

import java.math.BigDecimal;
import java.util.Map;

public class RequestValidator {

    private static final String WALLET_ID = "walletId";
    private static final String AMOUNT = "amount";

    private RequestValidator() {}

    public static void validateDepositRequest(Map<String, Object> request) {
        validateWalletAndAmount(request);
    }

    public static void validateWithdrawalRequest(Map<String, Object> request) {
        validateWalletAndAmount(request);
    }

    private static void validateWalletAndAmount(Map<String, Object> request) {
        if (!request.containsKey(WALLET_ID)) {
            throw new IllegalArgumentException("Invalid request: missing walletId");
        }
        if (!request.containsKey(AMOUNT)) {
            throw new IllegalArgumentException("Invalid request: missing amount");
        }
        
        try {
            Long.valueOf(request.get(WALLET_ID).toString());
            new BigDecimal(request.get(AMOUNT).toString());
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
        if (!request.containsKey(AMOUNT)) {
            throw new IllegalArgumentException("Invalid request: missing amount");
        }
        
        try {
            Long.valueOf(request.get("fromWalletId").toString());
            Long.valueOf(request.get("toWalletId").toString());
            new BigDecimal(request.get(AMOUNT).toString());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid request: invalid data format");
        }
    }
}