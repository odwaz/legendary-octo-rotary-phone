package com.merchant.wallet.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @Autowired
    private WalletUserRepository walletUserRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private final Map<String, String> otpStore = new ConcurrentHashMap<>();
    private final Random random = new Random();

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
        System.out.println("Attempting to send OTP for email: " + email);
        return validateCredentials(email, password)
            .then(Mono.fromRunnable(() -> {
                String otp = String.format("%06d", random.nextInt(1000000));
                otpStore.put(email, otp);
                System.out.println("📱 OTP for " + email + ": " + otp);
            }));
    }

    public Mono<String> verifyOtp(String email, String otp) {
        System.out.println("Verifying OTP for email: " + email + ", OTP: " + otp);
        String storedOtp = otpStore.get(email);
        System.out.println("Stored OTP: " + storedOtp);
        if (storedOtp == null || !storedOtp.equals(otp)) {
            System.out.println("OTP verification failed");
            return Mono.error(new RuntimeException("Invalid OTP"));
        }
        System.out.println("OTP verified successfully");
        
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
                if (exists) {
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
