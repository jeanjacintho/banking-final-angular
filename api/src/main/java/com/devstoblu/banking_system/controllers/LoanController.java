package com.devstoblu.banking_system.controllers;

import com.devstoblu.banking_system.dto.loan.LoanRequestDTO;
import com.devstoblu.banking_system.dto.loan.LoanSimulationDTO;
import com.devstoblu.banking_system.dto.loan.LoanResponseDTO;
import com.devstoblu.banking_system.services.LoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService){
        this.loanService = loanService;
    }

    @PostMapping("/simulate")
    public ResponseEntity<LoanSimulationDTO> simulate(@RequestBody LoanRequestDTO request){
        LoanSimulationDTO sim = loanService.simulateLoan(request);
        return ResponseEntity.ok(sim);
    }

    @PostMapping("/request")
    public ResponseEntity<LoanResponseDTO> requestLoan(@RequestBody LoanRequestDTO request){
        LoanResponseDTO resp = loanService.createLoanRequest(request);
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public ResponseEntity<List<LoanResponseDTO>> listUserLoans(@RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(loanService.listLoans(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanResponseDTO> getLoan(@PathVariable Long id){
        return ResponseEntity.ok(loanService.getLoanResponse(id));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<LoanResponseDTO> approve(@PathVariable Long id){
        return ResponseEntity.ok(loanService.approveLoan(id));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<LoanResponseDTO> reject(@PathVariable Long id){
        return ResponseEntity.ok(loanService.rejectLoan(id));
    }
}
