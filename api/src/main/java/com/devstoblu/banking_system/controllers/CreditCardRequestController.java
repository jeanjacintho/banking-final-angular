package com.devstoblu.banking_system.controllers;

import com.devstoblu.banking_system.dtos.CreditCardRequestDTO;
import com.devstoblu.banking_system.dtos.CreditCardRequestResponseDTO;
import com.devstoblu.banking_system.services.credit_card.CreditCardApplicationService;
import jakarta.validation.Valid;
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
    private final com.devstoblu.banking_system.repositories.UsuarioRepository usuarioRepository;

    public CreditCardRequestController(CreditCardApplicationService service, com.devstoblu.banking_system.repositories.UsuarioRepository usuarioRepository) {
        this.service = service;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/requests")
    public ResponseEntity<CreditCardRequestResponseDTO> request(
            @Valid @RequestBody CreditCardRequestDTO dto,
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails principal) {
        
        String cpf = principal.getUsername();
        com.devstoblu.banking_system.models.Usuario user = (com.devstoblu.banking_system.models.Usuario) usuarioRepository.findByCpf(cpf);
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }
        
        CreditCardRequestResponseDTO res = service.processApplication(dto, user.getId());
        HttpStatus status = switch (res.statusSolicitacao()) {
            case "aprovado" -> HttpStatus.CREATED;
            case "pendente" -> HttpStatus.ACCEPTED;
            default -> HttpStatus.OK;
        };
        return ResponseEntity.status(status).body(res);
    }
}
