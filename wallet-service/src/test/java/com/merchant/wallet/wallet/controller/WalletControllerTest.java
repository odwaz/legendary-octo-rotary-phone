package com.merchant.wallet.wallet.controller;

import com.merchant.wallet.wallet.domain.Wallet;
import com.merchant.wallet.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebFluxTest(WalletController.class)
class WalletControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private WalletService walletService;

    @MockBean
    private WebClient.Builder webClientBuilder;

    @Test
    void shouldGetWalletById() {
        Wallet wallet = new Wallet();
        wallet.setWalletId(1L);
        wallet.setBalance(new BigDecimal("100.00"));
        
        when(walletService.getWalletById(1L)).thenReturn(Mono.just(wallet));

        webTestClient.get()
                .uri("/api/v1/wallets/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Wallet.class)
                .value(result -> {
                    assertEquals(wallet.getWalletId(), result.getWalletId());
                    assertEquals(wallet.getBalance(), result.getBalance());
                });
    }

    @Test
    void shouldReturnNotFoundForNonExistentWallet() {
        when(walletService.getWalletById(999L)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/v1/wallets/999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldProcessTopupSuccessfully() {
        Map<String, Object> response = Map.of("status", "success", "newBalance", "150.00");
        when(walletService.processTopUpByAlias(anyString(), any(BigDecimal.class), anyString()))
                .thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/api/v1/wallets/topup")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                    "walletAlias", "john_wallet",
                    "amount", "50.00",
                    "description", "Top up"
                ))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .isEqualTo(response);
    }

    @Test
    void shouldProcessTransferSuccessfully() {
        Map<String, Object> response = Map.of("status", "success", "transactionId", "TXN123");
        when(walletService.processPaymentByAlias(anyString(), anyString(), any(BigDecimal.class), anyString()))
                .thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/api/v1/wallets/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                    "fromWalletAlias", "john_wallet",
                    "toWalletAlias", "jane_wallet",
                    "amount", "25.00",
                    "description", "Transfer"
                ))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .isEqualTo(response);
    }

    @Test
    void shouldUpdateBalanceSuccessfully() {
        Wallet updatedWallet = new Wallet();
        updatedWallet.setWalletId(1L);
        updatedWallet.setBalance(new BigDecimal("150.00"));
        
        when(walletService.updateBalance(eq(1L), any(BigDecimal.class), anyString()))
                .thenReturn(Mono.just(updatedWallet));

        webTestClient.put()
                .uri("/api/v1/wallets/1/balance")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                    "amount", "50.00",
                    "operation", "credit"
                ))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Wallet.class)
                .value(result -> {
                    assertEquals(updatedWallet.getWalletId(), result.getWalletId());
                    assertEquals(updatedWallet.getBalance(), result.getBalance());
                });
    }
}