package com.merchant.wallet.transaction.repository;

import com.merchant.wallet.transaction.domain.Transaction;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TransactionRepository extends R2dbcRepository<Transaction, Long> {
    Flux<Transaction> findBySenderWalletId(String senderWalletId);
    Flux<Transaction> findByRecipientWalletId(String recipientWalletId);
}
