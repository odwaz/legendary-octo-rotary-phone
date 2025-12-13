package com.merchant.wallet.wallet.service;

import com.merchant.wallet.wallet.domain.Wallet;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class WalletCreatorTest {

    @Test
    void shouldCreateWalletWithValidUserId() {
        WalletCreator creator = new WalletCreator();
        
        Wallet wallet = creator.createWallet(123L);
        
        assertNotNull(wallet);
        assertEquals(123L, wallet.getUserId());
        assertEquals(BigDecimal.ZERO, wallet.getBalance());
    }
    
    @Test
    void shouldThrowExceptionForNullUserId() {
        WalletCreator creator = new WalletCreator();
        
        assertThrows(IllegalArgumentException.class, () -> {
            creator.createWallet(null);
        });
    }
}