package com.merchant.wallet.wallet.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Wallet Service API",
        version = "1.0.0",
        description = "Wallet management and transaction operations"
    )
)
public class OpenApiConfig {
}