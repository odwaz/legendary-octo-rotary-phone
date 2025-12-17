package com.merchant.wallet.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        logger.info("[GATEWAY] → {} {} - User-Agent: {}", 
            request.getMethod(), 
            request.getPath(), 
            request.getHeaders().getFirst("User-Agent"));
        
        return chain.filter(exchange)
                .doFinally(signalType -> {
                    int statusCode = exchange.getResponse().getStatusCode() != null ? 
                        exchange.getResponse().getStatusCode().value() : 0;
                    logger.info("[GATEWAY] ← {} {} - Status: {} - {}", 
                        request.getMethod(), 
                        request.getPath(), 
                        statusCode,
                        statusCode >= 400 ? "ERROR" : "SUCCESS");
                })
                .doOnError(error -> logger.error("[GATEWAY] Request failed: {} {} - Error: {}", 
                    request.getMethod(), request.getPath(), error.getMessage()));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}