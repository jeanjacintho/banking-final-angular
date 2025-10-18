package com.devstoblu.banking_system.controller;

import com.devstoblu.banking_system.dtos.CreditCardRequestDTO;
import com.devstoblu.banking_system.dtos.CreditCardRequestResponseDTO;
import com.devstoblu.banking_system.services.credit_card.CreditCardApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/credit-cards")
public class CreditCardRequestController {
    private final CreditCardApplicationService service;

    public CreditCardRequestController(CreditCardApplicationService service) {
        this.service = service;
    }

    @PostMapping("/requests")
    public ResponseEntity<CreditCardRequestResponseDTO> request(@Valid @RequestBody CreditCardRequestDTO dto) {
        CreditCardRequestResponseDTO res = service.processApplication(dto);
        HttpStatus status = switch (res.statusSolicitacao()) {
            case "aprovado" -> HttpStatus.CREATED;
            case "pendente" -> HttpStatus.ACCEPTED;
            default -> HttpStatus.OK;
        };
        return ResponseEntity.status(status).body(res);
    }
}
