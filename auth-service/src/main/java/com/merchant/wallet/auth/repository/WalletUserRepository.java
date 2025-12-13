package com.merchant.wallet.auth.repository;

import com.merchant.wallet.auth.domain.WalletUser;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface WalletUserRepository extends R2dbcRepository<WalletUser, Long> {

    Mono<WalletUser> findByUserId(String userId);

    Flux<WalletUser> findByMerchantId(Long merchantId);

    Mono<WalletUser> findByEmail(String email);
    
    Mono<WalletUser> findByPhoneNumber(String phoneNumber);
}
