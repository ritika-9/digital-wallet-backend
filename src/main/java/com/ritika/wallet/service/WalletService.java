package com.ritika.wallet.service;

import com.ritika.wallet.entity.User;
import com.ritika.wallet.entity.Wallet;
import com.ritika.wallet.repo.UserRepository;
import com.ritika.wallet.repo.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionService transactionService;

    // Helper to get userId from email
    private Long getUserIdByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        return user.getId();
    }

    // Create wallet for user by email
    public Wallet createWalletForUser(String email) {
        Long userId = getUserIdByEmail(email);
        return createWalletForUser(userId);
    }

    // Create wallet for user by userId
    public Wallet createWalletForUser(Long userId) {
        if (walletRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("Wallet already exists for this user");
        }
        Wallet wallet = Wallet.builder()
                .userId(userId)
                .balance(BigDecimal.ZERO)
                .build();
        return walletRepository.save(wallet);
    }

    // Get wallet by userId
    public Wallet getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for userId: " + userId));
    }

    // Get wallet by email
    public Wallet getWalletByEmail(String email) {
        Long userId = getUserIdByEmail(email);
        return getWalletByUserId(userId);
    }

    // Deposit by userId
    public Wallet deposit(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        Wallet wallet = getWalletByUserId(userId);
        wallet.setBalance(wallet.getBalance().add(amount));
        transactionService.recordTransaction(wallet.getId(), amount, "DEPOSIT", "Deposit of " + amount);
        return walletRepository.save(wallet);
    }

    // Deposit by email
    public Wallet deposit(String email, BigDecimal amount) {
        Long userId = getUserIdByEmail(email);
        return deposit(userId, amount);
    }

    // Withdraw by userId
    public Wallet withdraw(Long userId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        Wallet wallet = getWalletByUserId(userId);
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
        transactionService.recordTransaction(wallet.getId(), amount, "WITHDRAW", "Withdrawal of " + amount);
        return walletRepository.save(wallet);
    }

    // Withdraw by email
    public Wallet withdraw(String email, BigDecimal amount) {
        Long userId = getUserIdByEmail(email);
        return withdraw(userId, amount);
    }
}
