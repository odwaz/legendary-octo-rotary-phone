package com.merchant.wallet.transaction.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransactionStatusManagerTest {

    @Test
    void shouldCreatePendingTransaction() {
        TransactionStatusManager manager = new TransactionStatusManager();
        
        TransactionStatus status = manager.createTransaction("TXN001");
        
        assertEquals("TXN001", status.getTransactionId());
        assertEquals("PENDING", status.getStatus());
        assertNotNull(status.getCreatedAt());
    }
    
    @Test
    void shouldUpdateTransactionToCompleted() {
        TransactionStatusManager manager = new TransactionStatusManager();
        manager.createTransaction("TXN001");
        
        boolean updated = manager.updateStatus("TXN001", "COMPLETED");
        
        assertTrue(updated);
        assertEquals("COMPLETED", manager.getStatus("TXN001").getStatus());
    }
    
    @Test
    void shouldUpdateTransactionToFailed() {
        TransactionStatusManager manager = new TransactionStatusManager();
        manager.createTransaction("TXN001");
        
        boolean updated = manager.updateStatus("TXN001", "FAILED");
        
        assertTrue(updated);
        assertEquals("FAILED", manager.getStatus("TXN001").getStatus());
    }
}