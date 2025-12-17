package com.merchant.wallet.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class WalletDto {
    private Long walletId;
    private Long userId;
    private Long merchantId;
    
    @NotNull
    @Positive
    private BigDecimal balance;
    
    private String currency;
    private String walletAlias;
    private boolean active;

    public Long getWalletId() { return walletId; }
    public void setWalletId(Long walletId) { this.walletId = walletId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }
    
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public String getWalletAlias() { return walletAlias; }
    public void setWalletAlias(String walletAlias) { this.walletAlias = walletAlias; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}