package com.merchant.wallet.transaction.service;

import com.merchant.wallet.transaction.domain.Transaction;
import com.merchant.wallet.transaction.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;
import java.time.LocalDateTime;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public Mono<Transaction> createTransaction(Transaction transaction) {
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setCreatedAt(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    public Mono<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    public Flux<Transaction> getTransactionsByWallet(String walletId) {
        return transactionRepository.findBySenderWalletId(walletId)
                .concatWith(transactionRepository.findByRecipientWalletId(walletId));
    }

    public Mono<Transaction> updateTransactionStatus(Long id, String status) {
        return transactionRepository.findById(id)
                .flatMap(transaction -> {
                    transaction.setStatus(status);
                    return transactionRepository.save(transaction);
                });
    }
}
