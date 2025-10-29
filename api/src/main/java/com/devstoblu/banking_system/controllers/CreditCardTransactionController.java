package com.devstoblu.banking_system.controllers;

import com.devstoblu.banking_system.models.CreditCardTransaction;
import com.devstoblu.banking_system.services.credit_card.CreditCardTransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/credit-cards/{cardId}/transactions")
public class CreditCardTransactionController {

    private final CreditCardTransactionService service;

    public CreditCardTransactionController(CreditCardTransactionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CreditCardTransaction>> list(@PathVariable("cardId") Long cardId) {
        return ResponseEntity.ok(service.listByCardId(cardId));
    }

    @PostMapping
    public ResponseEntity<CreditCardTransaction> create(@PathVariable("cardId") Long cardId,
                                                        @RequestBody Map<String, Object> body) {
        BigDecimal amount = new BigDecimal(String.valueOf(body.get("amount")));
        String merchantName = (String) body.getOrDefault("merchantName", "");
        String mcc = (String) body.getOrDefault("mcc", null);
        String category = (String) body.getOrDefault("category", null);
        Integer installmentsTotal = body.get("installmentsTotal") == null ? null : Integer.parseInt(String.valueOf(body.get("installmentsTotal")));

        CreditCardTransaction tx = service.authorizePurchase(
                cardId,
                amount,
                merchantName,
                mcc,
                category,
                installmentsTotal
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(tx);
    }
}



