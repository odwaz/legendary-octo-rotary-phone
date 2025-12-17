package com.merchant.wallet.webhook.service;

import com.merchant.wallet.webhook.util.RequestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
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
    private static final String WALLET_ID = "walletId";
    private static final String AMOUNT = "amount";
    private static final String DESCRIPTION = "description";
    private static final String STATUS = "status";
    private static final String SUCCESS = "success";
    private static final String ERROR = "error";
    private static final String TRANSACTION_ID = "transactionId";

    @Value("${wallet.service.url}")
    private String walletServiceUrl;
    private final WebClient.Builder webClientBuilder;
    private final LocalDateTime startTime;

    @Autowired
    public WebhookService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
        this.startTime = LocalDateTime.now();
    }

    public Mono<Map<String, Object>> processDeposit(Map<String, Object> request) {
        if (logger.isInfoEnabled()) {
            logger.info("[WEBHOOK] Processing deposit request");
        }
        
        return validateDepositRequest(request)
                .flatMap(validRequest -> {
                    Long walletId = Long.valueOf(validRequest.get(WALLET_ID).toString());
                    BigDecimal amount = new BigDecimal(validRequest.get(AMOUNT).toString());
                    String description = (String) validRequest.getOrDefault(DESCRIPTION, "External deposit");
                    
                    Map<String, Object> topupRequest = new HashMap<>();
                    topupRequest.put(WALLET_ID, walletId);
                    topupRequest.put(AMOUNT, amount);
                    topupRequest.put(DESCRIPTION, description);
                    
                    return webClientBuilder.build()
                            .post()
                            .uri(walletServiceUrl + "/api/v1/wallets/topup")
                            .bodyValue(topupRequest)
                            .retrieve()
                            .bodyToMono(Map.class)
                            .flatMap(transactionResponse -> 
                                webClientBuilder.build()
                                        .get()
                                        .uri(walletServiceUrl+"/api/v1/wallets/" + walletId)
                                        .retrieve()
                                        .bodyToMono(Map.class)
                                        .map(walletResponse -> {
                                            Map<String, Object> response = new HashMap<>();
                                            response.put(STATUS, SUCCESS);
                                            response.put(TRANSACTION_ID, transactionResponse.get(TRANSACTION_ID));
                                            response.put(AMOUNT, amount.toString());
                                            response.put("newBalance", walletResponse.get("balance"));
                                            if (logger.isInfoEnabled()) {
                                                logger.info("[WEBHOOK] Deposit processed successfully");
                                            }
                                            return response;
                                        })
                            );
                })
                .onErrorResume(error -> {
                    logger.error("[WEBHOOK] Deposit processing failed: {}", error.getMessage());
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put(STATUS, ERROR);
                    errorResponse.put(ERROR, error.getMessage());
                    return Mono.just(errorResponse);
                });
    }

    public Mono<Map<String, Object>> processWithdrawal(Map<String, Object> request) {
        if (logger.isInfoEnabled()) {
            logger.info("[WEBHOOK] Processing withdrawal request");
        }
        
        return validateWithdrawalRequest(request)
                .flatMap(validRequest -> {
                    Long walletId = Long.valueOf(validRequest.get(WALLET_ID).toString());
                    BigDecimal amount = new BigDecimal(validRequest.get(AMOUNT).toString());
                    
                    Map<String, Object> withdrawalRequest = new HashMap<>();
                    withdrawalRequest.put(AMOUNT, amount);
                    withdrawalRequest.put("operation", "SUBTRACT");
                    
                    return webClientBuilder.build()
                            .put()
                            .uri(walletServiceUrl + "/api/v1/wallets/" + walletId + "/balance")
                            .bodyValue(withdrawalRequest)
                            .retrieve()
                            .bodyToMono(Map.class)
                            .map(walletResponse -> {
                                Map<String, Object> response = new HashMap<>();
                                response.put(STATUS, SUCCESS);
                                response.put(AMOUNT, amount.toString());
                                response.put("newBalance", walletResponse.get("balance"));
                                if (logger.isInfoEnabled()) {
                                    logger.info("[WEBHOOK] Withdrawal processed successfully");
                                }
                                return response;
                            });
                })
                .onErrorResume(error -> {
                    logger.error("[WEBHOOK] Withdrawal processing failed: {}", error.getMessage());
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put(STATUS, ERROR);
                    errorResponse.put(ERROR, error.getMessage());
                    return Mono.just(errorResponse);
                });
    }

    public Mono<Map<String, Object>> processTransfer(Map<String, Object> request) {
        if (logger.isInfoEnabled()) {
            logger.info("[WEBHOOK] Processing transfer request");
        }
        
        return validateTransferRequest(request)
                .flatMap(validRequest -> {
                    Long fromWalletId = Long.valueOf(validRequest.get("fromWalletId").toString());
                    Long toWalletId = Long.valueOf(validRequest.get("toWalletId").toString());
                    BigDecimal amount = new BigDecimal(validRequest.get(AMOUNT).toString());
                    String description = (String) validRequest.getOrDefault(DESCRIPTION, "External transfer");
                    
                    Map<String, Object> transferRequest = new HashMap<>();
                    transferRequest.put("fromWalletId", fromWalletId);
                    transferRequest.put("toWalletId", toWalletId);
                    transferRequest.put(AMOUNT, amount);
                    transferRequest.put(DESCRIPTION, description);
                    
                    return webClientBuilder.build()
                            .post()
                            .uri(walletServiceUrl + "/api/v1/wallets/transfer")
                            .bodyValue(transferRequest)
                            .retrieve()
                            .bodyToMono(Map.class)
                            .map(transactionResponse -> {
                                Map<String, Object> response = new HashMap<>();
                                response.put(STATUS, SUCCESS);
                                response.put(TRANSACTION_ID, transactionResponse.get(TRANSACTION_ID));
                                response.put(AMOUNT, amount.toString());
                                if (logger.isInfoEnabled()) {
                                    logger.info("[WEBHOOK] Transfer processed successfully");
                                }
                                return response;
                            });
                })
                .onErrorResume(error -> {
                    logger.error("[WEBHOOK] Transfer processing failed: {}", error.getMessage());
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put(STATUS, ERROR);
                    errorResponse.put(ERROR, error.getMessage());
                    return Mono.just(errorResponse);
                });
    }

    public Mono<Map<String, Object>> getWebhookStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put(STATUS, "active");
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