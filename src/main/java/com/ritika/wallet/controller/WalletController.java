package com.ritika.wallet.controller;

import com.ritika.wallet.entity.Wallet;
import com.ritika.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    // Get wallet by userId
    @GetMapping("/by-id/{userId}")
    public ResponseEntity<?> getWalletById(@PathVariable Long userId) {
        Wallet wallet = walletService.getWalletByUserId(userId);
        return ResponseEntity.ok(Map.of(
                "id", wallet.getId(),
                "userId", wallet.getUserId(),
                "balance", wallet.getBalance()
        ));
    }

    // Get wallet by email
    @GetMapping("/by-email")
    public ResponseEntity<?> getWalletByEmail(@RequestParam String email) {
        Wallet wallet = walletService.getWalletByEmail(email);
        return ResponseEntity.ok(Map.of(
                "walletId", wallet.getId(),
                "userId", wallet.getUserId(),
                "balance", wallet.getBalance()
        ));
    }

    // Deposit by userId
    @PostMapping("/deposit-id/{userId}")
    public ResponseEntity<?> depositById(@PathVariable Long userId, @RequestParam BigDecimal amount) {
        Wallet wallet = walletService.deposit(userId, amount);
        return ResponseEntity.ok(Map.of(
                "message", "Deposit successful",
                "walletId", wallet.getId(),
                "balance", wallet.getBalance()
        ));
    }

    // Deposit by email
    @PostMapping("/deposit-email")
    public ResponseEntity<?> depositByEmail(@RequestParam String email, @RequestParam BigDecimal amount) {
        Wallet wallet = walletService.deposit(email, amount);
        return ResponseEntity.ok(Map.of(
                "message", "Deposit successful",
                "walletId", wallet.getId(),
                "balance", wallet.getBalance()
        ));
    }

    // Withdraw by userId
    @PostMapping("/withdraw-id/{userId}")
    public ResponseEntity<?> withdrawById(@PathVariable Long userId, @RequestParam BigDecimal amount) {
        Wallet wallet = walletService.withdraw(userId, amount);
        return ResponseEntity.ok(Map.of(
                "message", "Withdrawal successful",
                "walletId", wallet.getId(),
                "balance", wallet.getBalance()
        ));
    }

    // Withdraw by email
    @PostMapping("/withdraw-email")
    public ResponseEntity<?> withdrawByEmail(@RequestParam String email, @RequestParam BigDecimal amount) {
        Wallet wallet = walletService.withdraw(email, amount);
        return ResponseEntity.ok(Map.of(
                "message", "Withdrawal successful",
                "walletId", wallet.getId(),
                "balance", wallet.getBalance()
        ));
    }
}
