package com.devstoblu.banking_system.controllers;

import com.devstoblu.banking_system.models.investment.CDB;
import com.devstoblu.banking_system.models.investment.Investment;
import com.devstoblu.banking_system.models.investment.RendaFixa;
import com.devstoblu.banking_system.services.InvestmentService;
import com.devstoblu.banking_system.repositories.UsuarioRepository;
import com.devstoblu.banking_system.models.Usuario;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/investment")
public class InvestmentController {

  private final InvestmentService service;
  private final UsuarioRepository usuarioRepository;

  public InvestmentController(InvestmentService service, UsuarioRepository usuarioRepository) {
    this.service = service;
    this.usuarioRepository = usuarioRepository;
  }

  @GetMapping
  public ResponseEntity<List<Investment>> findAll() {
    return ResponseEntity.ok(service.findAll());
  }

  @GetMapping("/my")
  public ResponseEntity<List<Investment>> myInvestments(@AuthenticationPrincipal UserDetails principal) {
    if (principal == null) return ResponseEntity.status(401).build();
    String cpf = principal.getUsername();
    UserDetails userDetails = usuarioRepository.findByCpf(cpf);
    if (userDetails instanceof Usuario usuario) {
      return ResponseEntity.ok(service.findByUser(usuario.getId()));
    }
    return ResponseEntity.status(404).build();
  }

  @PostMapping("/cdb/{accountNumber}")
  public ResponseEntity<?> createInvestmentCdb(@PathVariable String accountNumber, @RequestBody Map<String, Number> data) {
    if (data == null || !data.containsKey("term") || !data.containsKey("value") || data.get("term") == null || data.get("value") == null) {
      Map<String, Object> error = new HashMap<>();
      error.put("error", "Payload inválido. Envie { term: number, value: number }.");
      return ResponseEntity.badRequest().body(error);
    }
    double term = data.get("term").doubleValue();
    double value = data.get("value").doubleValue();
    CDB newInvestment = service.createInvestmentCdb(accountNumber, term, value);
    return ResponseEntity.ok(newInvestment);
  }

  @PostMapping("/renda-fixa/{accountNumber}")
  public ResponseEntity<?> createInvestmentRendaFixa(@PathVariable String accountNumber, @RequestBody Map<String, Number> data) {
    if (data == null || !data.containsKey("value") || data.get("value") == null) {
      Map<String, Object> error = new HashMap<>();
      error.put("error", "Payload inválido. Envie { value: number }.");
      return ResponseEntity.badRequest().body(error);
    }
    double value = data.get("value").doubleValue();
    RendaFixa newInvestment = service.createInvestmentRenda(accountNumber, value);
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
  @PostMapping("/apply/{accountNumber}")
  public ResponseEntity<?> applyFee(@PathVariable String accountNumber, @RequestBody Map<String, Double> body) {
    try {
      service.applyInvestment(accountNumber, body.get("cdi"));

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

  @PostMapping("/withdraw/{accountNumber}/{id}")
  public ResponseEntity<?> withdraw(@PathVariable String accountNumber, @PathVariable Long id) {
    try {
      service.withdrawInvestment(accountNumber, id);

      Map<String, Object> response = new HashMap<>();
      response.put("message", "Investimento retirado da Renda Fixa");
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
