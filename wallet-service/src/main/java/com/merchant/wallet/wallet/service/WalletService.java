package com.merchant.wallet.wallet.service;

import com.merchant.wallet.wallet.domain.Wallet;
import com.merchant.wallet.wallet.repository.WalletRepository;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WalletService {

    private static final String STATUS = "status";
    private static final String TRANSACTIONS_URI = "/api/v1/transactions";

    private final WalletRepository walletRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${transaction.service.url}")
    private String transactionServiceUrl;
    private final CircuitBreaker circuitBreaker;

    public WalletService(WalletRepository walletRepository, WebClient.Builder webClientBuilder,CircuitBreaker circuitBreaker) {
        this.walletRepository = walletRepository;
        this.webClientBuilder = webClientBuilder;
        this.circuitBreaker = circuitBreaker;
    }

    public Mono<Wallet> getWalletById(Long walletId) {
        return walletRepository.findById(walletId);
    }

    public Mono<Wallet> getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId);
    }

    public Mono<Wallet> createWallet(Long userId, String currency, String walletAlias) {
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setCurrency(currency);
        wallet.setWalletAlias(walletAlias);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setActive(true);
        return walletRepository.save(wallet);
    }

    public Flux<Wallet> getAllWallets() {
        return walletRepository.findAll();
    }

    public Mono<Wallet> updateWallet(Long walletId, Wallet walletDetails) {
        return walletRepository.findById(walletId)
                .flatMap(wallet -> {
                    if (walletDetails.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                        return Mono.error(new IllegalArgumentException("Balance cannot be negative"));
                    }
                    wallet.setBalance(walletDetails.getBalance());
                    return walletRepository.save(wallet);
                });
    }

    public Mono<Wallet> updateBalance(Long walletId, BigDecimal amount, String operation) {
        return walletRepository.findById(walletId)
                .flatMap(wallet -> {
                    if ("ADD".equals(operation)) {
                        wallet.setBalance(wallet.getBalance().add(amount));
                    } else if ("SUBTRACT".equals(operation)) {
                        if (wallet.getBalance().compareTo(amount) < 0) {
                            return Mono.error(new RuntimeException("Insufficient balance"));
                        }
                        wallet.setBalance(wallet.getBalance().subtract(amount));
                    }
                    return walletRepository.save(wallet);
                });
    }

    @SuppressWarnings("rawtypes")
    public Mono<Map> processPayment(Long fromWalletId, Long toWalletId, BigDecimal amount, String description) {
        return walletRepository.findById(fromWalletId)
                .switchIfEmpty(Mono.error(new RuntimeException("Source wallet not found")))
                .zipWith(walletRepository.findById(toWalletId)
                        .switchIfEmpty(Mono.error(new RuntimeException("Destination wallet not found"))))
                .flatMap(wallets -> {
                    Wallet fromWallet = wallets.getT1();
                    Wallet toWallet = wallets.getT2();
                    
                    if (fromWallet.getBalance().compareTo(amount) < 0) {
                        return Mono.error(new RuntimeException("Insufficient balance"));
                    }
                    
                    fromWallet.setBalance(fromWallet.getBalance().subtract(amount));
                    toWallet.setBalance(toWallet.getBalance().add(amount));
                    
                    return walletRepository.save(fromWallet)
                            .then(walletRepository.save(toWallet))
                            .then(Mono.defer(() -> {
                                Map<String, Object> transactionRequest = new HashMap<>();
                                transactionRequest.put("senderWalletId", fromWallet.getWalletId().toString());
                                transactionRequest.put("recipientWalletId", toWallet.getWalletId().toString());
                                transactionRequest.put("merchantId", fromWallet.getMerchantId());
                                transactionRequest.put("amount", amount);
                                transactionRequest.put("transactionType", "TRANSFER");
                                transactionRequest.put("description", description);
                                transactionRequest.put(STATUS, "COMPLETED");
                                
                                return webClientBuilder.build()
                                        .post()
                                        .uri(transactionServiceUrl + TRANSACTIONS_URI)
                                        .bodyValue(transactionRequest)
                                        .retrieve()
                                        .bodyToMono(Map.class);
                            }));
                });
    }

    @SuppressWarnings("rawtypes")
    public Mono<Map> processTopUp(Long walletId, BigDecimal amount, String description) {
        return walletRepository.findById(walletId)
                .switchIfEmpty(Mono.error(new RuntimeException("Wallet not found")))
                .flatMap(wallet -> {
                    Map<String, Object> transactionRequest = new HashMap<>();
                    transactionRequest.put("senderWalletId", null);
                    transactionRequest.put("recipientWalletId", wallet.getWalletId().toString());
                    transactionRequest.put("merchantId", wallet.getMerchantId());
                    transactionRequest.put("amount", amount);
                    transactionRequest.put("transactionType", "DEPOSIT");
                    transactionRequest.put("description", description);
                    transactionRequest.put(STATUS, "PENDING");
                    
                    return webClientBuilder.build()
                            .post()
                            .uri(transactionServiceUrl + TRANSACTIONS_URI)
                            .bodyValue(transactionRequest)
                            .retrieve()
                            .bodyToMono(Map.class)
                            .flatMap(savedTransaction -> {
                                wallet.setBalance(wallet.getBalance().add(amount));
                                return walletRepository.save(wallet)
                                        .then(webClientBuilder.build()
                                                .put()
                                                .uri(transactionServiceUrl + TRANSACTIONS_URI + "/" + savedTransaction.get("id") + "/status?status=COMPLETED")
                                                .retrieve()
                                                .bodyToMono(Map.class));
                            })
                            .onErrorResume(e -> {
                                transactionRequest.put(STATUS, "FAILED");
                                return webClientBuilder.build()
                                        .post()
                                        .uri(transactionServiceUrl + TRANSACTIONS_URI)
                                        .bodyValue(transactionRequest)
                                        .retrieve()
                                        .bodyToMono(Map.class)
                                        .then(Mono.error(e));
                            });
                });
    }


    @SuppressWarnings("rawtypes")
    public Flux<Map> getTransactionsByWalletId(Long walletId) {
        return webClientBuilder.build()
                .get()
                .uri(transactionServiceUrl + TRANSACTIONS_URI + "/wallet/" + walletId)
                .retrieve()
                .bodyToFlux(Map.class)
                .transform(CircuitBreakerOperator.of(circuitBreaker))
                .onErrorResume(e -> Flux.just(Map.of("error", "Transaction service unavailable", "transactions", List.of())));
    }

    @SuppressWarnings("rawtypes")
    public Mono<Map> processTopUpByAlias(String walletAlias, BigDecimal amount, String description) {
        return walletRepository.findByWalletAlias(walletAlias)
                .switchIfEmpty(Mono.error(new RuntimeException("Wallet not found with alias: " + walletAlias)))
                .flatMap(wallet -> processTopUp(wallet.getWalletId(), amount, description));
    }

    @SuppressWarnings("rawtypes")
    public Mono<Map> processPaymentByAlias(String fromWalletAlias, String toWalletAlias, BigDecimal amount, String description) {
        return walletRepository.findByWalletAlias(fromWalletAlias)
                .switchIfEmpty(Mono.error(new RuntimeException("Source wallet not found with alias: " + fromWalletAlias)))
                .zipWith(walletRepository.findByWalletAlias(toWalletAlias)
                        .switchIfEmpty(Mono.error(new RuntimeException("Destination wallet not found with alias: " + toWalletAlias))))
                .flatMap(wallets -> {
                    Wallet fromWallet = wallets.getT1();
                    Wallet toWallet = wallets.getT2();
                    return processPayment(fromWallet.getWalletId(), toWallet.getWalletId(), amount, description);
                });
    }
}
