package com.ritika.wallet.controller;

import com.ritika.wallet.dto.LoginRequest;
import com.ritika.wallet.dto.LoginResponse;
import com.ritika.wallet.dto.RegisterRequest;
import com.ritika.wallet.dto.VerifyOtpRequest;
import com.ritika.wallet.entity.User;
import com.ritika.wallet.repo.UserRepository;
import com.ritika.wallet.security.JwtTokenUtil;
import com.ritika.wallet.service.OtpService;
import com.ritika.wallet.security.JwtUtil;
import com.ritika.wallet.security.CustomUserDetailsService;
import com.ritika.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService userDetailsService;
    private final WalletService walletService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already registered"));
        }

        // Create user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setKycStatus("PENDING"); // default

        User savedUser = userRepository.save(user);

        try {
            walletService.getWalletByUserId(user.getId());
            // Wallet exists, no need to create
        } catch (IllegalArgumentException e) {
            // Wallet not found, create one
            walletService.createWalletForUser(user.getId());
        }
        // Generate OTP
        String otp = otpService.generateOtpForUser(savedUser.getId());

        return ResponseEntity.ok(Map.of(
                "userId", savedUser.getId(),
                "otp", otp,
                "message", "User registered successfully. Please verify using the OTP."
        ));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request) {
        boolean isValid = otpService.verifyOtp(request.getUserId(), request.getOtp());
        if (!isValid) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired OTP"));
        }

        // OTP verified - KYC status remains pending
        userRepository.findById(request.getUserId()).ifPresent(user -> {
            user.setKycStatus("PENDING");
            userRepository.save(user);
        });

        return ResponseEntity.ok(Map.of("message", "OTP verified successfully."));
    }

    // New Login endpoint to authenticate user and generate JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            String token = jwtTokenUtil.generateToken(authentication);

            return ResponseEntity.ok(new LoginResponse(token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid email or password"));
        }
    }



}

// DTO for login request
@Data
class AuthRequest {
    private String email;
    private String password;
}

// DTO for login response
@Data
class AuthResponse {
    private final String jwt;
}
