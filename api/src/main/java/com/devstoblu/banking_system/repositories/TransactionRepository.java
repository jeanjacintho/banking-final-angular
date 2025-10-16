package com.devstoblu.banking_system.repositories;

import com.devstoblu.banking_system.models.Transaction;
import com.devstoblu.banking_system.models.banking_account.Account;
import com.devstoblu.banking_system.enums.TransferType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Contar transferências de um tipo específico entre duas datas
    long countByFromAccountAndTypeAndTimestampBetween(Account from, TransferType type, LocalDateTime start, LocalDateTime end);

    List<Transaction> findByFromAccountUsuarioIdOrToAccountUsuarioIdOrderByTimestampDesc(Long fromUserId, Long toUserId);
}
