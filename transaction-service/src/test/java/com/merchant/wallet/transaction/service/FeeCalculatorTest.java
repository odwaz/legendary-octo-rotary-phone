package com.merchant.wallet.transaction.service;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class FeeCalculatorTest {

    @Test
    void shouldCalculateTransferFee() {
        FeeCalculator calculator = new FeeCalculator();
        
        BigDecimal fee = calculator.calculateFee(new BigDecimal("100.00"), "TRANSFER");
        
        assertEquals(new BigDecimal("1.00"), fee);
    }
    
    @Test
    void shouldCalculateWithdrawalFee() {
        FeeCalculator calculator = new FeeCalculator();
        
        BigDecimal fee = calculator.calculateFee(new BigDecimal("100.00"), "WITHDRAWAL");
        
        assertEquals(new BigDecimal("2.00"), fee);
    }
}