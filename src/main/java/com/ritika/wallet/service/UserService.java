package com.ritika.wallet.service;

import com.ritika.wallet.dto.RegisterRequest;
import com.ritika.wallet.entity.User;
import com.ritika.wallet.exception.ResourceAlreadyExistsException;
import com.ritika.wallet.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final WalletService walletService; // Inject WalletService

    /**
     * Registers a new user:
     * 1. Checks if email/phone already exists.
     * 2. Saves the user.
     * 3. Creates a wallet with balance 0.
     * 4. Generates OTP for verification.
     *
     * @param req RegisterRequest DTO
     * @return OTP generated for the user
     */
    @Transactional
    public String registerUser(RegisterRequest req) {
        // Check if user already exists
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ResourceAlreadyExistsException("User with this email already exists");
        }
        if (userRepository.existsByPhone(req.getPhone())) {
            throw new ResourceAlreadyExistsException("User with this phone already exists");
        }

        // Create new user
        User user = new User(
                req.getName(),
                req.getEmail(),
                req.getPhone(),
                passwordEncoder.encode(req.getPassword()),
                "USER",
                "PENDING"
        );
        userRepository.save(user);

        // Create wallet for new user
        walletService.createWalletForUser(user.getId());

        // Generate OTP for user verification
        return otpService.generateOtpForUser(user.getId());
    }

    /**
     * Find user by ID
     */
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Find user by email
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    /**
     * Update KYC status of a user
     */
    public void markKycStatus(Long userId, String status) {
        userRepository.findById(userId).ifPresent(u -> {
            u.setKycStatus(status);
            userRepository.save(u);
        });
    }
}
