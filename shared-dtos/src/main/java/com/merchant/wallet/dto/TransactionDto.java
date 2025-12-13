package com.merchant.wallet.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class TransactionDto {
    private Long id;
    private String senderWalletId;
    private String recipientWalletId;
    private Long merchantId;
    
    @NotNull
    @Positive
    private BigDecimal amount;
    
    @NotNull
    private String transactionType;
    
    private String description;
    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getSenderWalletId() { return senderWalletId; }
    public void setSenderWalletId(String senderWalletId) { this.senderWalletId = senderWalletId; }
    
    public String getRecipientWalletId() { return recipientWalletId; }
    public void setRecipientWalletId(String recipientWalletId) { this.recipientWalletId = recipientWalletId; }
    
    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}