package com.devstoblu.banking_system.repositories;

import com.devstoblu.banking_system.models.banking_account.Account;
import com.devstoblu.banking_system.models.banking_account.CheckingAccount;
import com.devstoblu.banking_system.models.banking_account.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

  @Query("SELECT a FROM CheckingAccount a")
  List<CheckingAccount> findAllCheckingAccounts();

  @Query("SELECT a FROM SavingsAccount a")
  List<SavingsAccount> findAllSavingsAccounts();

  Optional<Account> findByAccountNumber(String accountNumber);
  
  List<Account> findByUsuarioId(Long userId);
}
