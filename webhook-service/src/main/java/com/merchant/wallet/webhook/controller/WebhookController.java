package com.merchant.wallet.webhook.controller;

import com.merchant.wallet.webhook.service.WebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/webhooks")
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);
    private final WebhookService webhookService;

    @Autowired
    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/deposit")
    public Mono<ResponseEntity<Map<String, Object>>> processDeposit(@RequestBody Map<String, Object> request) {
        logger.info("[WEBHOOK-CONTROLLER] → Deposit webhook - Request: {}", request);
        
        return webhookService.processDeposit(request)
                .map(response -> {
                    logger.info("[WEBHOOK-CONTROLLER] ← Deposit webhook - Response: {}", response);
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(error -> {
                    logger.error("[WEBHOOK-CONTROLLER] Deposit webhook error: {}", error.getMessage());
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    @PostMapping("/withdrawal")
    public Mono<ResponseEntity<Map<String, Object>>> processWithdrawal(@RequestBody Map<String, Object> request) {
        logger.info("[WEBHOOK-CONTROLLER] → Withdrawal webhook - Request: {}", request);
        
        return webhookService.processWithdrawal(request)
                .map(response -> {
                    logger.info("[WEBHOOK-CONTROLLER] ← Withdrawal webhook - Response: {}", response);
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(error -> {
                    logger.error("[WEBHOOK-CONTROLLER] Withdrawal webhook error: {}", error.getMessage());
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    @PostMapping("/transfer")
    public Mono<ResponseEntity<Map<String, Object>>> processTransfer(@RequestBody Map<String, Object> request) {
        logger.info("[WEBHOOK-CONTROLLER] → Transfer webhook - Request: {}", request);
        
        return webhookService.processTransfer(request)
                .map(response -> {
                    logger.info("[WEBHOOK-CONTROLLER] ← Transfer webhook - Response: {}", response);
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(error -> {
                    logger.error("[WEBHOOK-CONTROLLER] Transfer webhook error: {}", error.getMessage());
                    return Mono.just(ResponseEntity.internalServerError().build());
                });
    }

    @GetMapping("/status")
    public Mono<ResponseEntity<Map<String, Object>>> getWebhookStatus() {
        logger.info("[WEBHOOK-CONTROLLER] → Status check");
        
        return webhookService.getWebhookStatus()
                .map(response -> {
                    logger.info("[WEBHOOK-CONTROLLER] ← Status check - Response: {}", response);
                    return ResponseEntity.ok(response);
                });
    }
}