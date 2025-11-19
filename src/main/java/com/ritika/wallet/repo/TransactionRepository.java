package com.ritika.wallet.repo;

import com.ritika.wallet.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByWalletIdOrderByTimestampDesc(Long walletId);

    List<Transaction> findByWalletIdAndTypeOrderByTimestampDesc(Long walletId, String type);

    List<Transaction> findByWalletIdAndTimestampBetweenOrderByTimestampDesc(Long walletId, LocalDateTime start, LocalDateTime end);
}
