package com.devstoblu.banking_system.controllers;

import com.devstoblu.banking_system.enums.PixKeyType;
import com.devstoblu.banking_system.models.CreditCard;
import com.devstoblu.banking_system.models.CreditCardTransaction;
import com.devstoblu.banking_system.services.AccountService;
import com.devstoblu.banking_system.services.credit_card.CreditCardService;
import com.devstoblu.banking_system.services.credit_card.CreditCardTransactionService;
import com.devstoblu.banking_system.services.PixKeyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/gateway")
@CrossOrigin(origins = "*")
public class PaymentGatewayController {

    private final AccountService accountService;
    private final CreditCardService creditCardService;
    private final CreditCardTransactionService creditCardTransactionService;
    private final PixKeyService pixKeyService;

    public PaymentGatewayController(AccountService accountService,
                                    CreditCardService creditCardService,
                                    CreditCardTransactionService creditCardTransactionService,
                                    PixKeyService pixKeyService) {
        this.accountService = accountService;
        this.creditCardService = creditCardService;
        this.creditCardTransactionService = creditCardTransactionService;
        this.pixKeyService = pixKeyService;
    }

    @PostMapping("/purchase")
    public ResponseEntity<?> handlePurchase(@RequestBody Map<String, Object> payload) {
        try {
            String typePayment = String.valueOf(payload.getOrDefault("typePayment", "")).trim().toUpperCase();
            if (typePayment.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Campo 'typePayment' é obrigatório (PIX ou CREDIT)"
                ));
            }

            BigDecimal amount = new BigDecimal(String.valueOf(payload.get("amount")));
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "O valor da compra deve ser positivo"
                ));
            }

            String merchantName = String.valueOf(payload.getOrDefault("merchantName", "")).trim();
            if (merchantName.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Campo 'merchantName' é obrigatório"
                ));
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("amount", amount);
            result.put("merchantName", merchantName);
            result.put("typePayment", typePayment);

            switch (typePayment) {
                case "PIX" -> {
                    // Required: pixKeyType, pixKeyValue identify the payer's own account
                    String pixKeyTypeStr = String.valueOf(payload.getOrDefault("pixKeyType", "")).trim().toUpperCase();
                    String pixKeyValue = String.valueOf(payload.getOrDefault("pixKeyValue", "")).trim();

                    if (pixKeyTypeStr.isEmpty() || pixKeyValue.isEmpty()) {
                        return ResponseEntity.badRequest().body(Map.of(
                                "success", false,
                                "message", "Para PIX, 'pixKeyType' e 'pixKeyValue' são obrigatórios"
                        ));
                    }

                    PixKeyType pixKeyType = PixKeyType.valueOf(pixKeyTypeStr);

                    var payerAccount = pixKeyService.resolveAccountByKey(pixKeyType, pixKeyValue)
                            .orElseThrow(() -> new IllegalArgumentException("Chave PIX não encontrada"));

                    // Debit only the payer's account (purchase), no destination account
                    accountService.withdraw(payerAccount.getAccountNumber(), amount.doubleValue());

                    result.put("flow", "PIX_WITHDRAW");
                    result.put("payerAccount", payerAccount.getAccountNumber());
                    return ResponseEntity.status(HttpStatus.CREATED).body(result);
                }

                case "CREDIT" -> {
                    // Required: cardNumber
                    String cardNumber = String.valueOf(payload.getOrDefault("cardNumber", "")).trim();
                    if (cardNumber.isEmpty()) {
                        return ResponseEntity.badRequest().body(Map.of(
                                "success", false,
                                "message", "Para CREDIT, 'cardNumber' é obrigatório"
                        ));
                    }

                    String mcc = payload.get("mcc") == null ? null : String.valueOf(payload.get("mcc"));
                    String category = payload.get("category") == null ? null : String.valueOf(payload.get("category"));
                    Integer installmentsTotal = payload.get("installmentsTotal") == null ? null : Integer.parseInt(String.valueOf(payload.get("installmentsTotal")));

                    CreditCard card = creditCardService.getByCardNumber(cardNumber);
                    CreditCardTransaction tx = creditCardTransactionService.authorizePurchase(
                            card.getId(),
                            amount,
                            merchantName,
                            mcc,
                            category,
                            installmentsTotal
                    );

                    result.put("flow", "CREDIT_AUTHORIZATION");
                    result.put("transactionId", tx.getId());
                    result.put("status", tx.getStatus());
                    result.put("createdAt", tx.getCreatedAt());
                    return ResponseEntity.status(HttpStatus.CREATED).body(result);
                }

                default -> {
                    return ResponseEntity.badRequest().body(Map.of(
                            "success", false,
                            "message", "Tipo de pagamento inválido. Use PIX ou CREDIT"
                    ));
                }
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Erro ao processar compra: " + e.getMessage()
            ));
        }
    }
}


