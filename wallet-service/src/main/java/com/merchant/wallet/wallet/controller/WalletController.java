package com.merchant.wallet.wallet.controller;

import com.merchant.wallet.wallet.domain.Wallet;
import com.merchant.wallet.wallet.service.WalletService;
import com.merchant.wallet.dto.WalletDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/wallets")
@Tag(name = "Wallet", description = "Wallet management operations")
public class WalletController {

    private static final Logger logger = LoggerFactory.getLogger(WalletController.class);
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String CURRENCY = "currency";
    private static final String AMOUNT = "amount";

    private final WalletService walletService;
    private final WebClient.Builder webClientBuilder;

    @Value("${auth.service.url}")
    private String authServiceUrl;

    public WalletController(WalletService walletService, WebClient.Builder webClientBuilder) {
        this.walletService = walletService;
        this.webClientBuilder = webClientBuilder;
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Wallet>> getWallet(@PathVariable Long id) {
        return walletService.getWalletById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public Mono<ResponseEntity<Wallet>> getWalletByUser(@PathVariable Long userId) {
        return walletService.getWalletByUserId(userId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create wallet", description = "Create a new wallet for a user")
    public Mono<ResponseEntity<Wallet>> createWallet(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        String currency = (String) request.getOrDefault(CURRENCY, "ZAR");
        String walletAlias = (String) request.get("walletAlias");
        
        return walletService.createWallet(userId, currency, walletAlias)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}/balance")
    public Mono<ResponseEntity<Wallet>> updateBalance(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        BigDecimal amount = new BigDecimal(request.get(AMOUNT).toString());
        String operation = (String) request.get("operation");
        
        return walletService.updateBalance(id, amount, operation)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/topup")
    @Operation(summary = "Top up wallet", description = "Add funds to a wallet using wallet alias")
    public Mono<ResponseEntity<Map<String, Object>>> topup(@RequestBody Map<String, Object> request) {
        String walletAlias = (String) request.get("walletAlias");
        BigDecimal amount = new BigDecimal(request.get(AMOUNT).toString());
        String description = (String) request.getOrDefault("description", "Top up");
        
        return walletService.processTopUpByAlias(walletAlias, amount, description)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transfer funds", description = "Transfer funds between wallets using aliases")
    public Mono<ResponseEntity<Map<String, Object>>> transfer(@RequestBody Map<String, Object> request) {
        String fromWalletAlias = (String) request.get("fromWalletAlias");
        String toWalletAlias = (String) request.get("toWalletAlias");
        BigDecimal amount = new BigDecimal(request.get(AMOUNT).toString());
        String description = (String) request.getOrDefault("description", "Transfer");
        
        return walletService.processPaymentByAlias(fromWalletAlias, toWalletAlias, amount, description)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/user/balance")
    @Operation(summary = "Get user balance", description = "Get current user's wallet balance")
    public Mono<ResponseEntity<Map<String, Object>>> getUserBalance(@RequestHeader("Authorization") String authHeader) {
        // Extract email from JWT token
        String token = authHeader.replace(BEARER_PREFIX, "");
        String email = extractEmailFromToken(token);
        logger.info("[WALLET] → Balance request for user: {}", email);
        
        // Find user by email and get their wallet
        return webClientBuilder.build()
                .get()
                .uri(authServiceUrl + "/oauth/user/" + email)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(user -> {
                    Long userId = Long.valueOf(user.get("id").toString());
                    return walletService.getWalletByUserId(userId);
                })
                .map(wallet -> {
                    Map<String, Object> response = Map.of(
                        "balance", wallet.getBalance(),
                        CURRENCY, wallet.getCurrency(),
                        "walletId", wallet.getWalletId()
                    );
                    logger.info("[WALLET] ← Balance - Status: 200 - User: {} - Response: {}", email, response);
                    return ResponseEntity.ok(response);
                })
                .defaultIfEmpty(ResponseEntity.ok(Map.of(
                    "balance", BigDecimal.ZERO,
                    CURRENCY, "ZAR",
                    "message", "No wallet found"
                )))
                .doOnSuccess(response -> {
                    if (response.getBody().containsKey("message")) {
                        logger.warn("[WALLET] Balance - Status: 200 - User: {} - No wallet found", email);
                    }
                })
                .onErrorResume(error -> {
                    logger.error("[WALLET] Balance - Status: 400 - User: {} - Error: {}", email, error.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(Map.of("error", "Failed to retrieve balance")));
                });
    }
    
    private String extractEmailFromToken(String token) {
        // Simple JWT decode - in production use proper JWT library
        String[] parts = token.split("\\.");
        String payload = new String(java.util.Base64.getDecoder().decode(parts[1]));
        // Extract sub field which contains email
        return payload.split("\"sub\":\"")[1].split("\"")[0];
    }
}
