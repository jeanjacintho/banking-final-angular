package com.devstoblu.banking_system.controllers;

import com.devstoblu.banking_system.models.CreditCard;
import com.devstoblu.banking_system.models.Usuario;
import com.devstoblu.banking_system.repositories.UsuarioRepository;
import com.devstoblu.banking_system.services.credit_card.CreditCardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credit-cards")
public class CreditCardController {
    private final CreditCardService service;
    private final UsuarioRepository usuarioRepository;

    public CreditCardController(CreditCardService service, UsuarioRepository usuarioRepository) {
        this.service = service;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public ResponseEntity<List<CreditCard>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/my-cards")
    public ResponseEntity<List<CreditCard>> getMyCards(@AuthenticationPrincipal UserDetails principal) {
        String cpf = principal.getUsername();
        Usuario user = (Usuario) usuarioRepository.findByCpf(cpf);
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }
        List<CreditCard> userCards = service.getByUser(user);
        
        // Se o usuário não tem cartões associados, pegar todos e associar ao usuário
        if (userCards.isEmpty()) {
            List<CreditCard> allCards = service.getAll();
            for (CreditCard card : allCards) {
                if (card.getUsuario() == null) {
                    card.setUsuario(user);
                    service.update(card.getId(), card);
                }
            }
            userCards = service.getByUser(user);
        }
        
        return ResponseEntity.ok(userCards);
    }

    @GetMapping("/number/{cardNumber}")
    public ResponseEntity<CreditCard> getByCardNumber(@PathVariable String cardNumber) {
        return ResponseEntity.ok(service.getByCardNumber(cardNumber));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreditCard> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<CreditCard> create(@RequestBody CreditCard card) {
        return ResponseEntity.ok(service.create(card));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CreditCard> update(@PathVariable Long id, @RequestBody CreditCard updatedCard){
        return ResponseEntity.ok(service.update(id, updatedCard));
    }

    @GetMapping("/{id}/cvv")
    public ResponseEntity<CvvResponse> getCvv(@PathVariable Long id, @AuthenticationPrincipal UserDetails principal) {
        String cpf = principal.getUsername();
        Usuario user = (Usuario) usuarioRepository.findByCpf(cpf);
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }
        
        CreditCard card = service.getById(id);
        // Verificar se o cartão pertence ao usuário
        if (card.getUsuario() == null || !card.getUsuario().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }
        
        // Verificar se o CVV está disponível (cartões antigos podem não ter)
        if (card.getCvvEncrypted() == null || card.getCvvEncrypted().isEmpty()) {
            return ResponseEntity.status(404).body(new CvvResponse(null, "CVV não disponível para este cartão. Este cartão foi criado antes da implementação do sistema de CVV."));
        }
        
        try {
            String cvv = service.getDecryptedCvv(id);
            return ResponseEntity.ok(new CvvResponse(cvv, null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new CvvResponse(null, "Erro ao descriptografar CVV"));
        }
    }
    
    public record CvvResponse(String cvv, String error) {}

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
