package com.devstoblu.banking_system.services;

import com.devstoblu.banking_system.models.Usuario;
import com.devstoblu.banking_system.models.banking_account.Account;
import com.devstoblu.banking_system.models.banking_account.CheckingAccount;
import com.devstoblu.banking_system.models.banking_account.SavingsAccount;

import com.devstoblu.banking_system.repositories.UsuarioRepository;
import com.devstoblu.banking_system.repositories.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AccountService {
  private final AccountRepository accountRepository;
  private final UsuarioRepository usuarioRepository;

  public AccountService(AccountRepository repository, UsuarioRepository usuarioRepository) {
    this.accountRepository = repository;
    this.usuarioRepository = usuarioRepository;
  }

  public List<Account> findAll() {
    return accountRepository.findAll();
  }

  public List<CheckingAccount> findAllCheckingAccounts() {
    return accountRepository.findAllCheckingAccounts();
  }

  public List<SavingsAccount> findAllSavingsAccounts() {
    return accountRepository.findAllSavingsAccounts();
  }

  public CheckingAccount createCheckingAccount(Long userId, double balance) {
    Usuario usuario = usuarioRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

    boolean hasChecking = usuario.getAccounts().stream().anyMatch(a -> a instanceof CheckingAccount);
    if (hasChecking) throw new IllegalArgumentException("Usuário já possui uma conta corrente.");

    CheckingAccount account = new CheckingAccount();
    account.setBalance(balance);
    account.setUsuario(usuario);

    return accountRepository.save(account);
  }

  public SavingsAccount createSavingsAccount(Long userId, double balance) {
    Usuario usuario = usuarioRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

    boolean hasSavings = usuario.getAccounts().stream().anyMatch(a -> a instanceof SavingsAccount);
    if (hasSavings) throw new IllegalArgumentException("Usuário já possui uma conta poupança.");

    SavingsAccount account = new SavingsAccount();
    account.setBalance(balance);
    account.setUsuario(usuario);

    return accountRepository.save(account);
  }

}
