package com.merchant.wallet.transaction.service;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class TransactionValidatorTest {

    @Test
    void shouldValidateValidTransaction() {
        TransactionValidator validator = new TransactionValidator();
        TransactionRequest request = new TransactionRequest();
        request.setFromWalletId("WALLET001");
        request.setToWalletId("WALLET002");
        request.setAmount(new BigDecimal("100.00"));
        request.setType("TRANSFER");
        
        boolean isValid = validator.isValidTransaction(request);
        
        assertTrue(isValid);
    }
    
    @Test
    void shouldRejectNegativeAmount() {
        TransactionValidator validator = new TransactionValidator();
        TransactionRequest request = new TransactionRequest();
        request.setFromWalletId("WALLET001");
        request.setToWalletId("WALLET002");
        request.setAmount(new BigDecimal("-50.00"));
        request.setType("TRANSFER");
        
        boolean isValid = validator.isValidTransaction(request);
        
        assertFalse(isValid);
    }
    
    @Test
    void shouldRejectSameWalletTransfer() {
        TransactionValidator validator = new TransactionValidator();
        TransactionRequest request = new TransactionRequest();
        request.setFromWalletId("WALLET001");
        request.setToWalletId("WALLET001");
        request.setAmount(new BigDecimal("100.00"));
        request.setType("TRANSFER");
        
        boolean isValid = validator.isValidTransaction(request);
        
        assertFalse(isValid);
    }
}