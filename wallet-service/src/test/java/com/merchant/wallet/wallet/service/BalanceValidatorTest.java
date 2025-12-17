package com.merchant.wallet.wallet.service;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class BalanceValidatorTest {

    @Test
    void shouldValidateSufficientFunds() {
        BalanceValidator validator = new BalanceValidator();
        
        boolean isValid = validator.hasSufficientFunds(new BigDecimal("100.00"), new BigDecimal("50.00"));
        
        assertTrue(isValid);
    }
    
    @Test
    void shouldDetectInsufficientFunds() {
        BalanceValidator validator = new BalanceValidator();
        
        boolean isValid = validator.hasSufficientFunds(new BigDecimal("30.00"), new BigDecimal("50.00"));
        
        assertFalse(isValid);
    }
    
    @Test
    void shouldRejectNegativeAmount() {
        BalanceValidator validator = new BalanceValidator();
        
        assertThrows(IllegalArgumentException.class, () -> validator.validateAmount(new BigDecimal("-10.00")));
    }
    
    @Test
    void shouldAcceptPositiveAmount() {
        BalanceValidator validator = new BalanceValidator();
        
        assertDoesNotThrow(() -> validator.validateAmount(new BigDecimal("10.00")));
    }
}