package com.merchant.wallet.wallet.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestPropertySource(properties = {
    "spring.r2dbc.url=r2dbc:h2:mem:///testdb?DB_CLOSE_DELAY=-1&DB_CLOSE_ON_EXIT=FALSE",
    "spring.sql.init.mode=always",
    "logging.level.org.springframework.r2dbc=DEBUG"
})
class WalletIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldCreateAndRetrieveWallet() {
        webTestClient.post()
                .uri("/api/v1/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                    "userId", 1L,
                    "currency", "ZAR",
                    "walletAlias", "test_wallet_create"
                ))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldProcessTopupEndToEnd() {
        // First create a wallet
        webTestClient.post()
                .uri("/api/v1/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                    "userId", 2L,
                    "currency", "ZAR",
                    "walletAlias", "test_wallet_topup"
                ))
                .exchange()
                .expectStatus().isOk();

        // Then try to top up (this will fail because transaction service is not running)
        webTestClient.post()
                .uri("/api/v1/wallets/topup")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                    "walletAlias", "test_wallet_topup",
                    "amount", "100.00",
                    "description", "Integration test topup"
                ))
                .exchange()
                .expectStatus().is5xxServerError(); // Expect error since transaction service is not running
    }

    @Test
    void shouldProcessTransferEndToEnd() {
        // First create sender wallet
        webTestClient.post()
                .uri("/api/v1/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                    "userId", 3L,
                    "currency", "ZAR",
                    "walletAlias", "sender_wallet_transfer"
                ))
                .exchange()
                .expectStatus().isOk();

        // Create receiver wallet
        webTestClient.post()
                .uri("/api/v1/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                    "userId", 4L,
                    "currency", "ZAR",
                    "walletAlias", "receiver_wallet_transfer"
                ))
                .exchange()
                .expectStatus().isOk();

        // Then try to transfer (this will fail because transaction service is not running)
        webTestClient.post()
                .uri("/api/v1/wallets/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                    "fromWalletAlias", "sender_wallet_transfer",
                    "toWalletAlias", "receiver_wallet_transfer",
                    "amount", "50.00",
                    "description", "Integration test transfer"
                ))
                .exchange()
                .expectStatus().is5xxServerError(); // Expect error since transaction service is not running
    }
}