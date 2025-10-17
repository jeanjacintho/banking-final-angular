package com.devstoblu.banking_system.controllers;

import com.devstoblu.banking_system.models.banking_account.Account;
import com.devstoblu.banking_system.models.banking_account.CheckingAccount;
import com.devstoblu.banking_system.models.investment.CDB;
import com.devstoblu.banking_system.models.investment.Investment;
import com.devstoblu.banking_system.services.AccountService;
import com.devstoblu.banking_system.services.InvestmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/investment")
public class InvestmentController {

  private final InvestmentService service;

  public InvestmentController(InvestmentService service) {
    this.service = service;
  }

  @PostMapping("/cdb/{accountNumber}")
  public ResponseEntity<CDB> createInvestmentCdb(@PathVariable String accountNumber, @RequestBody Map<String, Double> data)  {
    CDB newInvestment = service.createInvestment(accountNumber, data.get("term"), data.get("value"));
    return ResponseEntity.ok(newInvestment);
  }

  @PostMapping("/cdb/applyInvestimento/{accountNumber}")
  public ResponseEntity<Investment> applyFee(@PathVariable String accountNumber) {
    service.applyInvestimento(accountNumber);
    return null;
  }
}
