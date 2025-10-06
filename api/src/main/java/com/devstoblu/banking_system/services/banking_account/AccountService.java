package com.devstoblu.banking_system.services.banking_account;

import com.devstoblu.banking_system.models.Usuario;
import com.devstoblu.banking_system.models.banking_account.Account;
import com.devstoblu.banking_system.models.banking_account.CheckingAccount;
import com.devstoblu.banking_system.models.banking_account.SavingsAccount;

import com.devstoblu.banking_system.repositories.UsuarioRepository;
import com.devstoblu.banking_system.repositories.banking_account.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AccountService {
  private final AccountRepository accountrepository;
  private final UsuarioRepository usuarioRepository;

  public AccountService(AccountRepository repository, UsuarioRepository usuarioRepository) {
    this.accountrepository = repository;
    this.usuarioRepository = usuarioRepository;
  }

  public List<Account> findAll() {
    return accountrepository.findAll();
  }

  public List<CheckingAccount> findAllCheckingAccounts() {
    return accountrepository.findAllCheckingAccounts();
  }

  public List<SavingsAccount> findAllSavingsAccounts() {
    return accountrepository.findAllSavingsAccounts();
  }

  public CheckingAccount createCheckingAccount(Long userId, double balance) {
    Usuario usuario = usuarioRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

    CheckingAccount account = new CheckingAccount();
    account.setBalance(balance);
    account.setUsuario(usuario);

    return accountrepository.save(account);
  }

  public SavingsAccount createSavingsAccount(Long userId, double balance) {
    Usuario usuario = usuarioRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

    SavingsAccount account = new SavingsAccount();
    account.setBalance(balance);
    account.setUsuario(usuario);

    return accountrepository.save(account);
  }

}
