package com.merchant.wallet.transaction.controller;

import com.merchant.wallet.transaction.domain.Transaction;
import com.merchant.wallet.transaction.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public Mono<ResponseEntity<Transaction>> createTransaction(@RequestBody Transaction transaction) {
        logger.info("[TRANSACTION] → Create - Request: {type: {}, amount: {}, description: {}}", 
            transaction.getTransactionType(), transaction.getAmount(), transaction.getDescription());
        return transactionService.createTransaction(transaction)
                .map(savedTransaction -> {
                    logger.info("[TRANSACTION] ← Create - Status: 200 - Response: {id: {}, status: {}, amount: {}}", 
                        savedTransaction.getId(), savedTransaction.getStatus(), savedTransaction.getAmount());
                    return ResponseEntity.ok(savedTransaction);
                })
                .onErrorResume(error -> {
                    logger.error("[TRANSACTION] Failed to create transaction - Error: {}", error.getMessage());
                    return Mono.error(error);
                });
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Transaction>> getTransaction(@PathVariable Long id) {
        return transactionService.getTransactionById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/status")
    public Mono<ResponseEntity<String>> getTransactionStatus(@PathVariable Long id) {
        return transactionService.getTransactionById(id)
                .map(transaction -> ResponseEntity.ok(transaction.getStatus()))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/wallet/{walletId}")
    public Flux<Transaction> getTransactionsByWallet(@PathVariable String walletId) {
        logger.info("[TRANSACTION] → Get transactions for wallet: {}", walletId);
        return transactionService.getTransactionsByWallet(walletId)
                .doOnComplete(() -> logger.info("[TRANSACTION] ← Transactions retrieved for wallet: {}", walletId))
                .doOnError(error -> logger.error("[TRANSACTION] Failed to retrieve transactions for wallet: {} - Error: {}", walletId, error.getMessage()));
    }

    @PutMapping("/{id}/status")
    public Mono<ResponseEntity<Transaction>> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        logger.info("[TRANSACTION] Updating transaction status - ID: {}, New Status: {}", id, status);
        return transactionService.updateTransactionStatus(id, status)
                .map(updatedTransaction -> {
                    logger.info("[TRANSACTION] Transaction status updated successfully - ID: {}, Status: {}", id, status);
                    return ResponseEntity.ok(updatedTransaction);
                })
                .onErrorResume(error -> {
                    logger.error("[TRANSACTION] Failed to update transaction status - ID: {}, Error: {}", id, error.getMessage());
                    return Mono.error(error);
                });
    }
}
