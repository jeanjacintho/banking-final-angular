package com.devstoblu.banking_system.repositories;

import com.devstoblu.banking_system.models.CreditCard;
import com.devstoblu.banking_system.models.CreditCardTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditCardTransactionRepository extends JpaRepository<CreditCardTransaction, Long> {
    List<CreditCardTransaction> findByCreditCardOrderByCreatedAtDesc(CreditCard creditCard);
}




