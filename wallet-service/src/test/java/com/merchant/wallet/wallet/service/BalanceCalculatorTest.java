package com.merchant.wallet.wallet.service;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BalanceCalculatorTest {

    @Test
    void shouldCalculateBalanceWithNoTransactions() {
        BalanceCalculator calculator = new BalanceCalculator();
        
        BigDecimal balance = calculator.calculateBalance(BigDecimal.ZERO);
        
        assertEquals(BigDecimal.ZERO, balance);
    }
    
    @Test
    void shouldAddCreditToBalance() {
        BalanceCalculator calculator = new BalanceCalculator();
        
        BigDecimal balance = calculator.addCredit(new BigDecimal("100.00"), new BigDecimal("50.00"));
        
        assertEquals(new BigDecimal("150.00"), balance);
    }
    
    @Test
    void shouldSubtractDebitFromBalance() {
        BalanceCalculator calculator = new BalanceCalculator();
        
        BigDecimal balance = calculator.subtractDebit(new BigDecimal("100.00"), new BigDecimal("30.00"));
        
        assertEquals(new BigDecimal("70.00"), balance);
    }
}