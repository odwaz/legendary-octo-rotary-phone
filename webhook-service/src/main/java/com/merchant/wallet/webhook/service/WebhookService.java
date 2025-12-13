package com.merchant.wallet.webhook.service;

import com.merchant.wallet.webhook.util.RequestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class WebhookService {

    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);
    private final WebClient.Builder webClientBuilder;
    private final LocalDateTime startTime;

    @Autowired
    public WebhookService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
        this.startTime = LocalDateTime.now();
    }

    public Mono<Map<String, Object>> processDeposit(Map<String, Object> request) {
        logger.info("[WEBHOOK] Processing deposit request: {}", request);
        
        return validateDepositRequest(request)
                .flatMap(validRequest -> {
                    Long walletId = Long.valueOf(validRequest.get("walletId").toString());
                    BigDecimal amount = new BigDecimal(validRequest.get("amount").toString());
                    String description = (String) validRequest.getOrDefault("description", "External deposit");
                    
                    Map<String, Object> topupRequest = new HashMap<>();
                    topupRequest.put("walletId", walletId);
                    topupRequest.put("amount", amount);
                    topupRequest.put("description", description);
                    
                    return webClientBuilder.build()
                            .post()
                            .uri("http://localhost:8002/api/v1/wallets/topup")
                            .bodyValue(topupRequest)
                            .retrieve()
                            .bodyToMono(Map.class)
                            .flatMap(transactionResponse -> 
                                webClientBuilder.build()
                                        .get()
                                        .uri("http://localhost:8002/api/v1/wallets/" + walletId)
                                        .retrieve()
                                        .bodyToMono(Map.class)
                                        .map(walletResponse -> {
                                            Map<String, Object> response = new HashMap<>();
                                            response.put("status", "success");
                                            response.put("transactionId", transactionResponse.get("transactionId"));
                                            response.put("amount", amount.toString());
                                            response.put("newBalance", walletResponse.get("balance"));
                                            logger.info("[WEBHOOK] Deposit processed successfully: {}", response);
                                            return response;
                                        })
                            );
                })
                .onErrorResume(error -> {
                    logger.error("[WEBHOOK] Deposit processing failed: {}", error.getMessage());
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("status", "error");
                    errorResponse.put("error", error.getMessage());
                    return Mono.just(errorResponse);
                });
    }

    public Mono<Map<String, Object>> processWithdrawal(Map<String, Object> request) {
        logger.info("[WEBHOOK] Processing withdrawal request: {}", request);
        
        return validateWithdrawalRequest(request)
                .flatMap(validRequest -> {
                    Long walletId = Long.valueOf(validRequest.get("walletId").toString());
                    BigDecimal amount = new BigDecimal(validRequest.get("amount").toString());
                    
                    Map<String, Object> withdrawalRequest = new HashMap<>();
                    withdrawalRequest.put("amount", amount);
                    withdrawalRequest.put("operation", "SUBTRACT");
                    
                    return webClientBuilder.build()
                            .put()
                            .uri("http://localhost:8002/api/v1/wallets/" + walletId + "/balance")
                            .bodyValue(withdrawalRequest)
                            .retrieve()
                            .bodyToMono(Map.class)
                            .map(walletResponse -> {
                                Map<String, Object> response = new HashMap<>();
                                response.put("status", "success");
                                response.put("amount", amount.toString());
                                response.put("newBalance", walletResponse.get("balance"));
                                logger.info("[WEBHOOK] Withdrawal processed successfully: {}", response);
                                return response;
                            });
                })
                .onErrorResume(error -> {
                    logger.error("[WEBHOOK] Withdrawal processing failed: {}", error.getMessage());
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("status", "error");
                    errorResponse.put("error", error.getMessage());
                    return Mono.just(errorResponse);
                });
    }

    public Mono<Map<String, Object>> processTransfer(Map<String, Object> request) {
        logger.info("[WEBHOOK] Processing transfer request: {}", request);
        
        return validateTransferRequest(request)
                .flatMap(validRequest -> {
                    Long fromWalletId = Long.valueOf(validRequest.get("fromWalletId").toString());
                    Long toWalletId = Long.valueOf(validRequest.get("toWalletId").toString());
                    BigDecimal amount = new BigDecimal(validRequest.get("amount").toString());
                    String description = (String) validRequest.getOrDefault("description", "External transfer");
                    
                    Map<String, Object> transferRequest = new HashMap<>();
                    transferRequest.put("fromWalletId", fromWalletId);
                    transferRequest.put("toWalletId", toWalletId);
                    transferRequest.put("amount", amount);
                    transferRequest.put("description", description);
                    
                    return webClientBuilder.build()
                            .post()
                            .uri("http://localhost:8002/api/v1/wallets/transfer")
                            .bodyValue(transferRequest)
                            .retrieve()
                            .bodyToMono(Map.class)
                            .map(transactionResponse -> {
                                Map<String, Object> response = new HashMap<>();
                                response.put("status", "success");
                                response.put("transactionId", transactionResponse.get("transactionId"));
                                response.put("amount", amount.toString());
                                logger.info("[WEBHOOK] Transfer processed successfully: {}", response);
                                return response;
                            });
                })
                .onErrorResume(error -> {
                    logger.error("[WEBHOOK] Transfer processing failed: {}", error.getMessage());
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("status", "error");
                    errorResponse.put("error", error.getMessage());
                    return Mono.just(errorResponse);
                });
    }

    public Mono<Map<String, Object>> getWebhookStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "active");
        status.put("uptime", calculateUptime());
        status.put("lastProcessed", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return Mono.just(status);
    }

    private Mono<Map<String, Object>> validateDepositRequest(Map<String, Object> request) {
        try {
            RequestValidator.validateDepositRequest(request);
            return Mono.just(request);
        } catch (IllegalArgumentException e) {
            return Mono.error(e);
        }
    }

    private Mono<Map<String, Object>> validateWithdrawalRequest(Map<String, Object> request) {
        try {
            RequestValidator.validateWithdrawalRequest(request);
            return Mono.just(request);
        } catch (IllegalArgumentException e) {
            return Mono.error(e);
        }
    }

    private Mono<Map<String, Object>> validateTransferRequest(Map<String, Object> request) {
        try {
            RequestValidator.validateTransferRequest(request);
            return Mono.just(request);
        } catch (IllegalArgumentException e) {
            return Mono.error(e);
        }
    }

    private String calculateUptime() {
        LocalDateTime now = LocalDateTime.now();
        long hours = java.time.Duration.between(startTime, now).toHours();
        long minutes = java.time.Duration.between(startTime, now).toMinutes() % 60;
        return String.format("%dh %dm", hours, minutes);
    }
}