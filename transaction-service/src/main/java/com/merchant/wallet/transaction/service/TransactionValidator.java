package com.merchant.wallet.transaction.service;

import java.math.BigDecimal;

public class TransactionValidator {
    
    public boolean isValidTransaction(TransactionRequest request) {
        return request.getFromWalletId() != null && 
               request.getToWalletId() != null && 
               request.getAmount() != null && 
               request.getAmount().compareTo(BigDecimal.ZERO) > 0 &&
               request.getType() != null &&
               !request.getFromWalletId().equals(request.getToWalletId());
    }
}