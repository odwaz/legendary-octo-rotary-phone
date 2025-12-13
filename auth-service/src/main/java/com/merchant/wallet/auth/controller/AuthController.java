package com.merchant.wallet.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.merchant.wallet.auth.service.AuthService;
import reactor.core.publisher.Mono;
import java.util.Map;

@RestController
@Tag(name = "Authentication", description = "OAuth2 authentication operations")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/oauth/login")
    public Mono<ResponseEntity<Map<String, String>>> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        
        return authService.sendOtp(email, password)
                .then(Mono.just(ResponseEntity.ok(Map.of("message", "Credentials verified. OTP sent to your phone."))))
                .onErrorReturn(ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials")));
    }

    @PostMapping("/oauth/send-otp")
    @Operation(summary = "Send OTP", description = "Send OTP to user's phone after credential verification")
    public Mono<ResponseEntity<Map<String, String>>> sendOtp(@RequestBody Map<String, String> request) {
        String identifier = request.get("email") != null ? request.get("email") : request.get("phoneNumber");
        String password = request.get("password");
        logger.info("[AUTH] → Send OTP - Request: {}", request.toString().replaceAll("password=[^,}]*", "password=***"));
        System.out.println("[AUTH] Send OTP request for: " + identifier);
        
        return authService.sendOtp(identifier, password)
                .then(Mono.just(ResponseEntity.ok(Map.of("message", "OTP sent successfully"))))
                .doOnSuccess(response -> logger.info("[AUTH] ← OTP sent - Status: {} - User: {} - Response: {}", 
                    response.getStatusCode().value(), identifier, response.getBody()))
                .onErrorReturn(ResponseEntity.badRequest().body(Map.of("error", "Failed to send OTP")))
                .doOnError(error -> logger.error("[AUTH] OTP failed - Status: 400 - User: {} - Error: {}", 
                    identifier, error.getMessage()));
    }

    @PostMapping("/oauth2/token")
    @Operation(summary = "OAuth2 Token", description = "Get access token using OTP grant type")
    public Mono<ResponseEntity<Map>> token(@RequestBody Map<String, String> request) {
        String grantType = request.get("grant_type");
        logger.info("[AUTH] → OAuth2 token - Request: {}", request.toString());
        System.out.println("[AUTH] OAuth2 token request - grant_type: " + grantType);
        
        if ("otp".equals(grantType)) {
            String identifier = request.get("username") != null ? request.get("username") : request.get("phone_number");
            String otp = request.get("otp_code");
            logger.info("[AUTH] OTP verification for: {}, OTP: {}", identifier, otp);
            System.out.println("[AUTH] OTP verification for: " + identifier + ", OTP: " + otp);
            
            return authService.verifyOtp(identifier, otp)
                    .map(token -> {
                        Map response = Map.of(
                            "access_token", token.substring(0, 20) + "...",
                            "token_type", "Bearer",
                            "expires_in", 3600,
                            "scope", "read write"
                        );
                        logger.info("[AUTH] ← OAuth2 token - Status: 200 - User: {} - Response: {}", identifier, response);
                        return ResponseEntity.ok((Map) Map.of(
                            "access_token", token,
                            "token_type", "Bearer",
                            "expires_in", 3600,
                            "scope", "read write"
                        ));
                    })
                    .onErrorReturn(ResponseEntity.badRequest().body((Map) Map.of(
                        "error", "invalid_grant",
                        "error_description", "Invalid OTP"
                    )))
                    .doOnError(error -> logger.error("[AUTH] OAuth2 token - Status: 400 - User: {} - Error: {}", 
                        identifier, error.getMessage()));
        }
        
        return Mono.just(ResponseEntity.badRequest().body((Map) Map.of(
            "error", "unsupported_grant_type",
            "error_description", "Grant type not supported"
        )));
    }

    @PostMapping("/oauth/register")
    @Operation(summary = "Register User", description = "Register a new user account")
    public Mono<ResponseEntity<Map<String, String>>> register(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        String fullName = request.get("fullName");
        String phoneNumber = request.get("phoneNumber");
        
        return authService.registerUser(email, password, fullName, phoneNumber)
                .then(Mono.just(ResponseEntity.ok(Map.of("message", "User registered successfully"))))
                .onErrorReturn(ResponseEntity.badRequest().body(Map.of("error", "Registration failed")));
    }
    
    @GetMapping("/oauth/user/{email}")
    public Mono<ResponseEntity<Map>> getUserByEmail(@PathVariable String email) {
        return authService.getUserByEmail(email)
                .map(user -> ResponseEntity.ok((Map) Map.of(
                    "id", user.getId(),
                    "email", user.getEmail(),
                    "role", user.getRole()
                )))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
