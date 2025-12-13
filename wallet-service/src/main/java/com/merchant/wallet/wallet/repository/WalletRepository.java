package com.merchant.wallet.wallet.repository;

import com.merchant.wallet.wallet.domain.Wallet;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface WalletRepository extends R2dbcRepository<Wallet, Long> {
    Mono<Wallet> findByUserId(Long userId);
    Mono<Wallet> findByWalletAlias(String walletAlias);
}
