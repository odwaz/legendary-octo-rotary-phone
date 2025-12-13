package com.merchant.wallet.transaction.service;

import java.time.LocalDateTime;

public class TransactionStatus {
    private String transactionId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public TransactionStatus(String transactionId, String status) {
        this.transactionId = transactionId;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}