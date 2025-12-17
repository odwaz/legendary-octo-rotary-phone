package com.merchant.wallet.transaction.controller;

import com.merchant.wallet.transaction.domain.Transaction;
import com.merchant.wallet.transaction.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebFluxTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private TransactionService transactionService;

    @Test
    void shouldCreateTransactionSuccessfully() {
        Transaction transaction = new Transaction();
        transaction.setTransactionId("TXN123");
        transaction.setStatus("COMPLETED");
        when(transactionService.createTransaction(any())).thenReturn(Mono.just(transaction));

        webTestClient.post()
                .uri("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                    "fromWalletId", "WALLET001",
                    "toWalletId", "WALLET002",
                    "amount", "100.00",
                    "type", "TRANSFER",
                    "description", "Test transfer"
                ))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Transaction.class)
                .value(result -> result.getTransactionId().equals("TXN123"));
    }



    @Test
    void shouldGetTransactionsByWalletId() {
        Transaction transaction1 = new Transaction();
        transaction1.setId(1L);
        transaction1.setSenderWalletId("1");
        
        Transaction transaction2 = new Transaction();
        transaction2.setId(2L);
        transaction2.setRecipientWalletId("1");
        
        when(transactionService.getTransactionsByWallet("1"))
                .thenReturn(Flux.just(transaction1, transaction2));

        webTestClient.get()
                .uri("/api/v1/transactions/wallet/1")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Transaction.class)
                .hasSize(2);
    }

    @Test
    void shouldUpdateTransactionStatus() {
        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setStatus("COMPLETED");
        when(transactionService.updateTransactionStatus(anyLong(), anyString()))
                .thenReturn(Mono.just(updatedTransaction));

        webTestClient.put()
                .uri("/api/v1/transactions/1/status?status=COMPLETED")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Transaction.class)
                .value(result -> result.getStatus().equals("COMPLETED"));
    }
}