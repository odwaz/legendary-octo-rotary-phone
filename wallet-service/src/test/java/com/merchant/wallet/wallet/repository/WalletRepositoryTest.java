package com.merchant.wallet.wallet.repository;

import com.merchant.wallet.wallet.domain.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.TestPropertySource;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

@DataR2dbcTest
@TestPropertySource(properties = {
    "spring.r2dbc.url=r2dbc:h2:mem:///testdb?DB_CLOSE_DELAY=-1&DB_CLOSE_ON_EXIT=FALSE",
    "spring.sql.init.mode=always"
})
class WalletRepositoryTest {

    @Autowired
    private WalletRepository walletRepository;

    @Test
    void shouldSaveAndFindWallet() {
        Wallet wallet = new Wallet();
        wallet.setUserId(1L);
        wallet.setBalance(new BigDecimal("100.00"));
        wallet.setCurrency("ZAR");
        wallet.setWalletAlias("test_wallet");

        StepVerifier.create(walletRepository.save(wallet))
                .expectNextMatches(saved -> saved.getWalletId() != null)
                .verifyComplete();
    }

    @Test
    void shouldFindWalletByUserId() {
        Wallet wallet = new Wallet();
        wallet.setUserId(123L);
        wallet.setBalance(new BigDecimal("200.00"));
        wallet.setCurrency("ZAR");
        wallet.setWalletAlias("user123_wallet");

        StepVerifier.create(walletRepository.save(wallet)
                .then(walletRepository.findByUserId(123L)))
                .expectNextMatches(found -> 
                    found.getUserId().equals(123L) && 
                    found.getBalance().equals(new BigDecimal("200.00")))
                .verifyComplete();
    }

    @Test
    void shouldFindWalletByAlias() {
        Wallet wallet = new Wallet();
        wallet.setUserId(456L);
        wallet.setBalance(new BigDecimal("300.00"));
        wallet.setCurrency("ZAR");
        wallet.setWalletAlias("unique_alias");

        StepVerifier.create(walletRepository.save(wallet)
                .then(walletRepository.findByWalletAlias("unique_alias")))
                .expectNextMatches(found -> 
                    found.getWalletAlias().equals("unique_alias") && 
                    found.getUserId().equals(456L))
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyForNonExistentWallet() {
        StepVerifier.create(walletRepository.findById(999L))
                .verifyComplete();
    }

    @Test
    void shouldUpdateWalletBalance() {
        Wallet wallet = new Wallet();
        wallet.setUserId(789L);
        wallet.setBalance(new BigDecimal("100.00"));
        wallet.setCurrency("ZAR");
        wallet.setWalletAlias("update_test");

        StepVerifier.create(walletRepository.save(wallet)
                .flatMap(saved -> {
                    saved.setBalance(new BigDecimal("150.00"));
                    return walletRepository.save(saved);
                }))
                .expectNextMatches(updated -> 
                    updated.getBalance().equals(new BigDecimal("150.00")))
                .verifyComplete();
    }
}