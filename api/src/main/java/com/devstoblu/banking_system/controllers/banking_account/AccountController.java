package com.devstoblu.banking_system.controllers.banking_account;

import com.devstoblu.banking_system.models.banking_account.Account;
import com.devstoblu.banking_system.models.banking_account.CheckingAccount;
import com.devstoblu.banking_system.models.banking_account.SavingsAccount;
import com.devstoblu.banking_system.services.banking_account.AccountService;
import com.devstoblu.banking_system.services.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountController {
  private final AccountService service;

  public AccountController(AccountService service) {
    this.service = service;
  }

  @GetMapping
  public ResponseEntity<List<Account>> findAll() {
    return ResponseEntity.ok(service.findAll());
  }

  @GetMapping("/checking")
  public ResponseEntity<List<CheckingAccount>> getAllChecking() {
    return ResponseEntity.ok(service.findAllCheckingAccounts());
  }

  @GetMapping("/savings")
  public ResponseEntity<List<SavingsAccount>> getAllSavings() {
    return ResponseEntity.ok(service.findAllSavingsAccounts());
  }

  @PostMapping("/checking/{userId}")
  public ResponseEntity<Account> createChecking(@PathVariable Long userId, @RequestBody CheckingAccount account) {
    CheckingAccount newAccount = service.createCheckingAccount(userId, account.getBalance());
    return ResponseEntity.ok(newAccount);
  }

  @PostMapping("/savings/{userId}")
  public ResponseEntity<Account> createSavings(@PathVariable Long userId, @RequestBody SavingsAccount account) {
    SavingsAccount newAccount = service.createSavingsAccount(userId, account.getBalance());
    return ResponseEntity.ok(newAccount);
  }
}
