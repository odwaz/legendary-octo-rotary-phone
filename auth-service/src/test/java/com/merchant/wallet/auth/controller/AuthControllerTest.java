package com.merchant.wallet.auth.controller;

import com.merchant.wallet.auth.config.JwtUtil;
import com.merchant.wallet.auth.repository.WalletUserRepository;
import com.merchant.wallet.auth.service.AuthService;
import com.merchant.wallet.auth.service.OtpGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@WebFluxTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private AuthService authService;

    @Autowired
    private OtpGenerator otpGenerator;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public AuthService authService() {
            return mock(AuthService.class);
        }
        
        @Bean
        @Primary
        public OtpGenerator otpGenerator() {
            return mock(OtpGenerator.class);
        }
        
        @Bean
        @Primary
        public WalletUserRepository walletUserRepository() {
            return mock(WalletUserRepository.class);
        }
        
        @Bean
        @Primary
        public PasswordEncoder passwordEncoder() {
            return mock(PasswordEncoder.class);
        }
        
        @Bean
        @Primary
        public JwtUtil jwtUtil() {
            return mock(JwtUtil.class);
        }
        
        @Bean
        @Primary
        public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
            return http.csrf(csrf -> csrf.disable())
                    .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
                    .build();
        }
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        when(authService.registerUser(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/oauth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                    "email", "test@example.com",
                    "password", "password123",
                    "fullName", "John Doe",
                    "phoneNumber", "1234567890"
                ))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldRejectInvalidRegistration() {
        when(authService.registerUser(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Mono.error(new RuntimeException("Invalid data")));

        webTestClient.post()
                .uri("/oauth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("email", "invalid"))
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void shouldSendOtpSuccessfully() {
        when(authService.sendOtp(anyString(), anyString())).thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/oauth/send-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("email", "test@example.com", "password", "password123"))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldVerifyOtpSuccessfully() {
        when(authService.verifyOtp(anyString(), anyString())).thenReturn(Mono.just("jwt-token"));

        webTestClient.post()
                .uri("/oauth2/token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("grant_type", "otp", "username", "test@example.com", "otp_code", "123456"))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldRejectInvalidOtp() {
        when(authService.verifyOtp(anyString(), anyString()))
                .thenReturn(Mono.error(new RuntimeException("Invalid OTP")));

        webTestClient.post()
                .uri("/oauth2/token")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("grant_type", "otp", "username", "test@example.com", "otp_code", "wrong"))
                .exchange()
                .expectStatus().is4xxClientError();
    }
}