package com.merchant.wallet.wallet.service;

import com.merchant.wallet.wallet.domain.Wallet;
import java.math.BigDecimal;

public class WalletCreator {
    
    public Wallet createWallet(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCurrency("ZAR");
        wallet.setActive(true);
        return wallet;
    }
}