package com.merchant.wallet.webhook.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebhookIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldReturnWebhookStatus() {
        webTestClient.get()
                .uri("/api/v1/webhooks/status")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .value(response -> {
                    assert response.containsKey("status");
                    assert response.containsKey("uptime");
                    assert response.get("status").equals("active");
                });
    }

    @Test
    void shouldValidateDepositRequest() {
        webTestClient.post()
                .uri("/api/v1/webhooks/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("invalid", "data"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .value(response -> {
                    assert response.get("status").equals("error");
                    assert response.containsKey("error");
                });
    }

    @Test
    void shouldValidateWithdrawalRequest() {
        webTestClient.post()
                .uri("/api/v1/webhooks/withdrawal")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("amount", "50.00"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .value(response -> {
                    assert response.get("status").equals("error");
                    assert response.containsKey("error");
                });
    }

    @Test
    void shouldValidateTransferRequest() {
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
                .value(response -> {
                    assert response.get("status").equals("error");
                    assert response.containsKey("error");
                });
    }
}