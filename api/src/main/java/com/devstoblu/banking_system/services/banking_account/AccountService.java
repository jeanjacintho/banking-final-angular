package com.devstoblu.banking_system.services.banking_account;

import com.devstoblu.banking_system.models.banking_account.Account;
import com.devstoblu.banking_system.models.banking_account.CheckingAccount;
import com.devstoblu.banking_system.models.banking_account.SavingsAccount;
import com.devstoblu.banking_system.repositories.banking_account.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AccountService {
  private final AccountRepository repository;

  public AccountService(AccountRepository repository) {
    this.repository = repository;
  }

  public List<Account> findAll() {
    return repository.findAll();
  }

  public List<CheckingAccount> findAllCheckingAccounts() {
    return repository.findAllCheckingAccounts();
  }

  public List<SavingsAccount> findAllSavingsAccounts() {
    return repository.findAllSavingsAccounts();
  }

  public CheckingAccount createCheckingAccount(double balance) {
    CheckingAccount account = new CheckingAccount();
    account.setBalance(balance);
    return repository.save(account);
  }

  public SavingsAccount createSavingsAccount(double balance) {
    SavingsAccount account = new SavingsAccount();
    account.setBalance(balance);
    return repository.save(account);
  }

}
