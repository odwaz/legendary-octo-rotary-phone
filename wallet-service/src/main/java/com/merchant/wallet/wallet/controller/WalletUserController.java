package com.merchant.wallet.wallet.controller;

import com.merchant.wallet.wallet.domain.Wallet;
import com.merchant.wallet.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/walletusers")
public class WalletUserController {

    @Autowired
    private WebClient.Builder webClientBuilder;
    
    @Autowired
    private WalletRepository walletRepository;

    @PostMapping("/register")
    public Mono<ResponseEntity<Map<String, String>>> register(@RequestBody Map<String, String> request) {
        // Call auth-service to create user
        return webClientBuilder.build()
                .post()
                .uri("http://localhost:8001/oauth/register")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(response -> {
                    // Get the created user to obtain the actual user ID
                    String email = request.get("email");
                    return webClientBuilder.build()
                            .get()
                            .uri("http://localhost:8001/oauth/user/" + email)
                            .retrieve()
                            .bodyToMono(Map.class)
                            .flatMap(user -> {
                                // Create wallet with actual user ID
                                Wallet wallet = new Wallet();
                                wallet.setUserId(Long.valueOf(user.get("id").toString()));
                                wallet.setMerchantId(1L); // Default merchant ID
                                wallet.setBalance(BigDecimal.ZERO);
                                wallet.setCurrency("ZAR");
                                wallet.setWalletAlias(generateWalletAlias(request.get("fullName")));
                                wallet.setActive(true);
                                
                                return walletRepository.save(wallet)
                                        .then(Mono.just(ResponseEntity.ok(Map.of("message", "User and wallet created successfully"))));
                            });
                })
                .onErrorReturn(ResponseEntity.badRequest().body(Map.of("error", "Registration failed")));
    }
    
    private String generateWalletAlias(String fullName) {
        if (fullName == null) fullName = "user";
        return fullName.replaceAll("\\s+", "").toLowerCase() + System.currentTimeMillis();
    }
}