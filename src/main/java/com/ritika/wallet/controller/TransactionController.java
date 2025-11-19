package com.ritika.wallet.controller;

import com.ritika.wallet.entity.Transaction;
import com.ritika.wallet.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // ✅ Get all transactions for a wallet
    @GetMapping("/{walletId}")
    public ResponseEntity<?> getTransactions(@PathVariable Long walletId) {
        List<Transaction> txns = transactionService.getTransactionsByWallet(walletId);
        if (txns.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "No transactions found for walletId " + walletId));
        }
        return ResponseEntity.ok(Map.of("transactions", txns));
    }

    // ✅ Get transactions filtered by type
    @GetMapping("/{walletId}/type/{type}")
    public ResponseEntity<?> getTransactionsByType(@PathVariable Long walletId, @PathVariable String type) {
        List<Transaction> txns = transactionService.getTransactionsByType(walletId, type.toUpperCase());
        if (txns.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "No " + type + " transactions found for walletId " + walletId));
        }
        return ResponseEntity.ok(Map.of("transactions", txns));
    }

    // ✅ Get transactions within a date range
    @GetMapping("/{walletId}/range")
    public ResponseEntity<?> getTransactionsByDateRange(
            @PathVariable Long walletId,
            @RequestParam String start,
            @RequestParam String end
    ) {
        LocalDateTime startDate = LocalDateTime.parse(start); // parse ISO string
        LocalDateTime endDate = LocalDateTime.parse(end);

        List<Transaction> txns = transactionService.getTransactionsByDateRange(walletId, startDate, endDate);
        if (txns.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "No transactions found between given dates"));
        }
        return ResponseEntity.ok(Map.of("transactions", txns));
    }

}
