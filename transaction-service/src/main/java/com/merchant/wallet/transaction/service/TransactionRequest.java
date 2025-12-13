package com.merchant.wallet.transaction.service;

import java.math.BigDecimal;

public class TransactionRequest {
    private String fromWalletId;
    private String toWalletId;
    private BigDecimal amount;
    private String type;
    
    public String getFromWalletId() {
        return fromWalletId;
    }
    
    public void setFromWalletId(String fromWalletId) {
        this.fromWalletId = fromWalletId;
    }
    
    public String getToWalletId() {
        return toWalletId;
    }
    
    public void setToWalletId(String toWalletId) {
        this.toWalletId = toWalletId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
}