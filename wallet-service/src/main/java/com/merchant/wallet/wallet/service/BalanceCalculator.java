package com.merchant.wallet.wallet.service;

import java.math.BigDecimal;

public class BalanceCalculator {
    
    public BigDecimal calculateBalance(BigDecimal initialBalance) {
        return initialBalance;
    }
    
    public BigDecimal addCredit(BigDecimal currentBalance, BigDecimal creditAmount) {
        return currentBalance.add(creditAmount);
    }
    
    public BigDecimal subtractDebit(BigDecimal currentBalance, BigDecimal debitAmount) {
        return currentBalance.subtract(debitAmount);
    }
}