package com.devstoblu.banking_system.controller;

import com.devstoblu.banking_system.models.Transferencia;
import com.devstoblu.banking_system.services.TransferenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/transferencias")
@RequiredArgsConstructor
public class TransferenciaController {

    private final TransferenciaService transferenciaService;

    @PostMapping("/internal")
    public ResponseEntity<Transferencia> transferInternal(
            @RequestParam Long sourceAccountId,
            @RequestParam Long targetAccountId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String description
    ) {
        Transferencia transferencia = transferenciaService.transfer(sourceAccountId, targetAccountId, amount, description);
        return ResponseEntity.ok(transferencia);
    }
}
