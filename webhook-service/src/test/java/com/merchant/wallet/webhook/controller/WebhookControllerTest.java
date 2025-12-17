package com.merchant.wallet.webhook.controller;

import com.merchant.wallet.webhook.service.WebhookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(WebhookController.class)
class WebhookControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private WebhookService webhookService;

    @Test
    void shouldProcessDepositWebhookSuccessfully() {
        // Given
        Map<String, Object> response = Map.of(
            "status", "success",
            "transactionId", "TXN123",
            "amount", "100.00"
        );
        when(webhookService.processDeposit(any())).thenReturn(Mono.just(response));

        // When & Then
        webTestClient.post()
                .uri("/api/v1/webhooks/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                    "walletId", 1,
                    "amount", "100.00",
                    "externalTransactionId", "EXT123",
                    "description", "Test deposit"
                ))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .isEqualTo(response);
    }

    @Test
    void shouldProcessWithdrawalWebhookSuccessfully() {
        // Given
        Map<String, Object> response = Map.of(
            "status", "success",
            "newBalance", "950.00",
            "amount", "50.00"
        );
        when(webhookService.processWithdrawal(any())).thenReturn(Mono.just(response));

        // When & Then
        webTestClient.post()
                .uri("/api/v1/webhooks/withdrawal")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                    "walletId", 1,
                    "amount", "50.00",
                    "externalTransactionId", "EXT124"
                ))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .isEqualTo(response);
    }

    @Test
    void shouldProcessTransferWebhookSuccessfully() {
        // Given
        Map<String, Object> response = Map.of(
            "status", "success",
            "transactionId", "TXN125",
            "amount", "25.00"
        );
        when(webhookService.processTransfer(any())).thenReturn(Mono.just(response));

        // When & Then
        webTestClient.post()
                .uri("/api/v1/webhooks/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                    "fromWalletId", 1,
                    "toWalletId", 2,
                    "amount", "25.00",
                    "externalTransactionId", "EXT125",
                    "description", "Test transfer"
                ))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .isEqualTo(response);
    }

    @Test
    void shouldHandleWebhookStatusCheck() {
        // Given
        Map<String, Object> response = Map.of(
            "status", "active",
            "uptime", "24h 30m",
            "lastProcessed", "2024-01-01T10:00:00"
        );
        when(webhookService.getWebhookStatus()).thenReturn(Mono.just(response));

        // When & Then
        webTestClient.get()
                .uri("/api/v1/webhooks/status")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .isEqualTo(response);
    }

    @Test
    void shouldReturnErrorForInvalidDepositRequest() {
        // Given
        Map<String, Object> errorResponse = Map.of(
            "status", "error",
            "error", "Invalid request: missing required fields"
        );
        when(webhookService.processDeposit(any())).thenReturn(Mono.just(errorResponse));

        // When & Then
        webTestClient.post()
                .uri("/api/v1/webhooks/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("invalid", "data"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .isEqualTo(errorResponse);
    }

    @Test
    void shouldReturnErrorForInvalidWithdrawalRequest() {
        // Given
        Map<String, Object> errorResponse = Map.of(
            "status", "error",
            "error", "Invalid request: missing walletId"
        );
        when(webhookService.processWithdrawal(any())).thenReturn(Mono.just(errorResponse));

        // When & Then
        webTestClient.post()
                .uri("/api/v1/webhooks/withdrawal")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("amount", "50.00"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .isEqualTo(errorResponse);
    }

    @Test
    void shouldReturnErrorForInvalidTransferRequest() {
        // Given
        Map<String, Object> errorResponse = Map.of(
            "status", "error",
            "error", "Invalid request: missing toWalletId"
        );
        when(webhookService.processTransfer(any())).thenReturn(Mono.just(errorResponse));

        // When & Then
        webTestClient.post()
                .uri("/api/v1/webhooks/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                    "fromWalletId", 1,
                    "amount", "25.00"
                ))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .isEqualTo(errorResponse);
    }

    @Test
    void shouldHandleServiceErrors() {
        // Given
        when(webhookService.processDeposit(any()))
                .thenReturn(Mono.error(new RuntimeException("Service unavailable")));

        // When & Then
        webTestClient.post()
                .uri("/api/v1/webhooks/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                    "walletId", 1,
                    "amount", "100.00"
                ))
                .exchange()
                .expectStatus().is5xxServerError();
    }
}