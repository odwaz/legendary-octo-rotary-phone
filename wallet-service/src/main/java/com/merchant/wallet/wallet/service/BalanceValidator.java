package com.merchant.wallet.wallet.service;

import java.math.BigDecimal;

public class BalanceValidator {
    
    public boolean hasSufficientFunds(BigDecimal currentBalance, BigDecimal amount) {
        return currentBalance.compareTo(amount) >= 0;
    }
    
    public void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
    }
}