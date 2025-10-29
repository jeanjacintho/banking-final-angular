package com.devstoblu.banking_system.controller;

import com.devstoblu.banking_system.models.CreditCard;
import com.devstoblu.banking_system.services.credit_card.CreditCardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credit-cards")
public class CreditCardController {
    private final CreditCardService service;

    public CreditCardController(CreditCardService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CreditCard>> getAll() {
        return ResponseEntity.ok(service.getAll());
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

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(service.delete(id));
    }

}
