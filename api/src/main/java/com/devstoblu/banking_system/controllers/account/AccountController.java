package com.devstoblu.banking_system.controllers.account;

import com.devstoblu.banking_system.models.account.Account;
import com.devstoblu.banking_system.services.account.AccountService;
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

  @PostMapping("/new")
  public ResponseEntity<Account> create(@RequestBody Account account) {
    return ResponseEntity.ok(service.create(account));
  }

  @GetMapping
  public ResponseEntity<List<Account>> findAll() {
    return ResponseEntity.ok(service.findAll());
  }
}
