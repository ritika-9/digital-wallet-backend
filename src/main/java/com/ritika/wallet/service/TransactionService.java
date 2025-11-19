package com.ritika.wallet.service;

import com.ritika.wallet.entity.Transaction;
import com.ritika.wallet.repo.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    // ✅ Record a transaction
    public Transaction recordTransaction(Long walletId, BigDecimal amount, String type, String description) {
        Transaction txn = Transaction.builder()
                .walletId(walletId)
                .amount(amount)
                .type(type.toUpperCase()) // Normalize type
                .description(description)
                .timestamp(LocalDateTime.now())
                .build();
        return transactionRepository.save(txn);
    }

    // ✅ Get all transactions for a wallet
    public List<Transaction> getTransactionsByWallet(Long walletId) {
        return transactionRepository.findByWalletIdOrderByTimestampDesc(walletId);
    }

    // ✅ Get transactions by type
    public List<Transaction> getTransactionsByType(Long walletId, String type) {
        return transactionRepository.findByWalletIdAndTypeOrderByTimestampDesc(walletId, type.toUpperCase());
    }

    // ✅ Get transactions within a date range
    public List<Transaction> getTransactionsByDateRange(Long walletId, LocalDateTime start, LocalDateTime end) {
        return transactionRepository.findByWalletIdAndTimestampBetweenOrderByTimestampDesc(walletId, start, end);
    }
}
