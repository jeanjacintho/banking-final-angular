package com.devstoblu.banking_system.controllers;

import com.devstoblu.banking_system.models.investment.CDB;
import com.devstoblu.banking_system.services.InvestmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/investment")
public class InvestmentController {

  private final InvestmentService service;

  public InvestmentController(InvestmentService service) {
    this.service = service;
  }

  @PostMapping("/cdb/{accountNumber}")
  public ResponseEntity<CDB> createInvestmentCdb(@PathVariable String accountNumber, @RequestBody Map<String, Double> data) {
    CDB newInvestment = service.createInvestment(accountNumber, data.get("term"), data.get("value"));
    return ResponseEntity.ok(newInvestment);
  }

  @DeleteMapping("/cdb/{accountNumber}/{id}")
  public ResponseEntity<?> deleteInvestment(@PathVariable String accountNumber, @PathVariable Long id) {
    try {
      service.deleteInvestment(accountNumber, id);

      Map<String, Object> response = new HashMap<>();
      response.put("message", "Investimento deletado com sucesso");
      response.put("account", accountNumber);
      response.put("id", id);

      return ResponseEntity.ok(response);

    } catch (RuntimeException e) {
      Map<String, Object> errorResponse = new HashMap<>();
      errorResponse.put("error", e.getMessage());
      errorResponse.put("account", accountNumber);
      errorResponse.put("id", id);

      return ResponseEntity.badRequest().body(errorResponse);
    }
  }

  // Somente para teste, será implementado para executar automaticamente
  @PostMapping("/cdb/apply/{accountNumber}")
  public ResponseEntity<?> applyFee(@PathVariable String accountNumber) {
    try {
      service.applyInvestimento(accountNumber);

      Map<String, Object> response = new HashMap<>();
      response.put("message", "Investimento aplicado");
      response.put("account", accountNumber);

      return ResponseEntity.ok(response);

    } catch (RuntimeException e) {
      Map<String, Object> errorResponse = new HashMap<>();
      errorResponse.put("error", e.getMessage());
      errorResponse.put("accountNumber", accountNumber);

      return ResponseEntity.badRequest().body(errorResponse);
    }
  }
}
