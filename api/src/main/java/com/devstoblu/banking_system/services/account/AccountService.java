package com.devstoblu.banking_system.services.account;

import com.devstoblu.banking_system.models.account.Account;
import com.devstoblu.banking_system.repositories.account.AccountRepository;
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

  public Account create(Account account) {
    return repository.save(account);
  }

  public List<Account> findAll() {
    return repository.findAll();
  }
}
