package com.merchant.wallet.transaction.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionStatusManager {
    private final Map<String, TransactionStatus> transactions = new ConcurrentHashMap<>();
    
    public TransactionStatus createTransaction(String transactionId) {
        TransactionStatus status = new TransactionStatus(transactionId, "PENDING");
        transactions.put(transactionId, status);
        return status;
    }
    
    public boolean updateStatus(String transactionId, String newStatus) {
        TransactionStatus status = transactions.get(transactionId);
        if (status != null) {
            status.setStatus(newStatus);
            return true;
        }
        return false;
    }
    
    public TransactionStatus getStatus(String transactionId) {
        return transactions.get(transactionId);
    }
}