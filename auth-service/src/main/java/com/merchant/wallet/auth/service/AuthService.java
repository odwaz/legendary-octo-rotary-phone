package com.merchant.wallet.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.merchant.wallet.auth.config.JwtUtil;
import com.merchant.wallet.auth.domain.WalletUser;
import com.merchant.wallet.auth.repository.WalletUserRepository;
import reactor.core.publisher.Mono;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private static final String EMAIL = "email";

    private final WalletUserRepository walletUserRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final Map<String, String> otpStore = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public AuthService(WalletUserRepository walletUserRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.walletUserRepository = walletUserRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public Mono<Void> validateCredentials(String email, String password) {
        return walletUserRepository.findByEmail(email)
            .switchIfEmpty(Mono.error(new RuntimeException("Invalid email or password")))
            .flatMap(user -> {
                if (!passwordEncoder.matches(password, user.getPassword())) {
                    return Mono.error(new RuntimeException("Invalid email or password"));
                }
                return Mono.empty();
            });
    }
    
    public Mono<Void> sendOtp(String email, String password) {
        logger.info("Attempting to send OTP for {}: {}", EMAIL, email);
        return validateCredentials(email, password)
            .then(Mono.fromRunnable(() -> {
                String otp = String.format("%06d", random.nextInt(1000000));
                otpStore.put(email, otp);
                logger.info("📱 OTP for {} {}: {}", EMAIL, email, otp);
            }));
    }

    public Mono<String> verifyOtp(String email, String otp) {
        logger.info("Verifying OTP for {}: {}, OTP: {}", EMAIL, email, otp);
        String storedOtp = otpStore.get(email);
        logger.debug("Stored OTP: {}", storedOtp);
        if (storedOtp == null || !storedOtp.equals(otp)) {
            logger.warn("OTP verification failed");
            return Mono.error(new RuntimeException("Invalid OTP"));
        }
        logger.info("OTP verified successfully");
        
        return walletUserRepository.findByEmail(email)
            .map(user -> user.getRole() != null ? user.getRole() : "USER")
            .defaultIfEmpty("USER")
            .map(role -> {
                otpStore.remove(email);
                return jwtUtil.generateToken(email, role);
            });
    }

    public Mono<Void> registerUser(String email, String password, String fullName, String phoneNumber) {
        return walletUserRepository.findByEmail(email)
            .hasElement()
            .flatMap(exists -> {
                if (Boolean.TRUE.equals(exists)) {
                    return Mono.error(new RuntimeException("Email already exists"));
                }
                WalletUser user = new WalletUser();
                user.setEmail(email);
                user.setPassword(passwordEncoder.encode(password));
                user.setFullName(fullName);
                user.setPhoneNumber(phoneNumber);
                user.setRole("USER");
                user.setActive(true);
                user.setMerchantId(1L); // Default merchant for now
                return walletUserRepository.save(user).then();
            });
    }
    
    public Mono<WalletUser> getUserByEmail(String email) {
        return walletUserRepository.findByEmail(email);
    }
}
