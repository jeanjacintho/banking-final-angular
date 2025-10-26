package com.devstoblu.banking_system.controllers;

import com.devstoblu.banking_system.dto.loan.LoanRequestDTO;
import com.devstoblu.banking_system.dto.loan.LoanSimulationDTO;
import com.devstoblu.banking_system.dto.loan.LoanResponseDTO;
import com.devstoblu.banking_system.services.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/simulate")
    public ResponseEntity<LoanSimulationDTO> simulate(@Valid @RequestBody LoanRequestDTO request) {
        LoanSimulationDTO sim = loanService.simulateLoan(request);
        return ResponseEntity.ok(sim);
    }

    @PostMapping("/request")
    public ResponseEntity<LoanResponseDTO> requestLoan(@Valid @RequestBody LoanRequestDTO request) {
        LoanResponseDTO resp = loanService.createLoanRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping
    public ResponseEntity<List<LoanResponseDTO>> listUserLoans(@RequestParam(required = false) Long userId) {
        List<LoanResponseDTO> loans = loanService.listLoans(userId);
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanResponseDTO> getLoan(@PathVariable Long id) {
        LoanResponseDTO loan = loanService.getLoanResponse(id);
        if (loan == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empréstimo não encontrado com id: " + id);
        }
        return ResponseEntity.ok(loan);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/approve")
    public ResponseEntity<LoanResponseDTO> approve(@PathVariable Long id) {
        LoanResponseDTO loan = loanService.approveLoan(id);
        if (loan == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empréstimo não encontrado para aprovação: " + id);
        }
        return ResponseEntity.ok(loan);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/reject")
    public ResponseEntity<LoanResponseDTO> reject(@PathVariable Long id) {
        LoanResponseDTO loan = loanService.rejectLoan(id);
        if (loan == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Empréstimo não encontrado para rejeição: " + id);
        }
        return ResponseEntity.ok(loan);
    }
}
