package com.devstoblu.banking_system.controllers;

import com.devstoblu.banking_system.dto.loan.LoanRequestDTO;
import com.devstoblu.banking_system.dto.loan.LoanSimulationDTO;
import com.devstoblu.banking_system.dto.loan.LoanResponseDTO;
import com.devstoblu.banking_system.services.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
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
        return ResponseEntity.ok(loan);
    }
}