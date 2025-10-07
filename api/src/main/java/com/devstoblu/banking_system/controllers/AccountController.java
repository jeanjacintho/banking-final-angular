package com.devstoblu.banking_system.controllers;

import com.devstoblu.banking_system.models.banking_account.Account;
import com.devstoblu.banking_system.models.banking_account.CheckingAccount;
import com.devstoblu.banking_system.models.banking_account.SavingsAccount;
import com.devstoblu.banking_system.services.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

  @GetMapping("/{accountNumber}")
  public ResponseEntity<Optional<Account>> findByAccountNumber(@PathVariable String accountNumber) {
    return ResponseEntity.ok(service.findByAccountNumber(accountNumber));
  }

  @PostMapping("/checking/{userId}")
  public ResponseEntity<CheckingAccount> createChecking(@PathVariable Long userId, @RequestBody CheckingAccount account) {
    CheckingAccount newAccount = service.createCheckingAccount(userId, account.getBalance());
    return ResponseEntity.ok(newAccount);
  }

  @PostMapping("/savings/{userId}")
  public ResponseEntity<SavingsAccount> createSavings(@PathVariable Long userId, @RequestBody SavingsAccount account) {
    SavingsAccount newAccount = service.createSavingsAccount(userId, account.getBalance());
    return ResponseEntity.ok(newAccount);
  }

  @PostMapping("/deposit/{accountNumber}")
  public ResponseEntity<?> deposit(@PathVariable String accountNumber, @RequestBody Map<String, Double> request) {
    try {
      Account updatedAccount = service.deposit(accountNumber, request.get("value"));
      return ResponseEntity.ok(updatedAccount);
    } catch (RuntimeException e) {

      // Mensagem de erro Json
      Map<String, Object> errorResponse = new HashMap<>();
      errorResponse.put("error", e.getMessage());
      errorResponse.put("accountNumber", accountNumber);

      return ResponseEntity.badRequest().body(errorResponse);
    }
  }

  @PostMapping("/withdraw/{accountNumber}")
  public ResponseEntity<?> withdraw(@PathVariable String accountNumber, @RequestBody Map<String, Double> request) {
    try {
      Account updatedAccount = service.withdraw(accountNumber, request.get("value"));
      return ResponseEntity.ok(updatedAccount);
    } catch (RuntimeException e) {

      // Mensagem de erro Json
      Map<String, Object> errorResponse = new HashMap<>();
      errorResponse.put("error", e.getMessage());
      errorResponse.put("accountNumber", accountNumber);

      return ResponseEntity.badRequest().body(errorResponse);
    }
  }

  @DeleteMapping("/{accountNumber}")
  public ResponseEntity<?> deleteChecking(@PathVariable String accountNumber) {
    try {
      service.delete(accountNumber);

      Map<String, Object> response = new HashMap<>();
      response.put("message", "Conta deletada com sucesso");
      response.put("accountNumber", accountNumber);

      return ResponseEntity.ok(response);
    } catch (RuntimeException e) {

      Map<String, Object> errorResponse = new HashMap<>();
      errorResponse.put("error", e.getMessage());
      errorResponse.put("accountNumber", accountNumber);

      return ResponseEntity.badRequest().body(errorResponse);
    }
  }

  @PostMapping("/fee-and-income")
  public ResponseEntity<AccountService.FeeApplicationResult> aplicarTaxas() {
    AccountService.FeeApplicationResult result = service.applyFeesAndMaintenanceWithDetails();
    return ResponseEntity.ok(result);
  }
}
