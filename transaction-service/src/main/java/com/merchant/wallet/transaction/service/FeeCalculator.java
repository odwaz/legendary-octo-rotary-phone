package com.merchant.wallet.transaction.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FeeCalculator {
    
    public BigDecimal calculateFee(BigDecimal amount, String transactionType) {
        switch (transactionType) {
            case "TRANSFER":
                return amount.multiply(new BigDecimal("0.01")).setScale(2, RoundingMode.HALF_UP);
            case "WITHDRAWAL":
                return amount.multiply(new BigDecimal("0.02")).setScale(2, RoundingMode.HALF_UP);
            default:
                return BigDecimal.ZERO;
        }
    }
}