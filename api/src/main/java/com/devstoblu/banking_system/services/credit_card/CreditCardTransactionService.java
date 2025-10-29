package com.devstoblu.banking_system.services.credit_card;

import com.devstoblu.banking_system.enums.CreditCardTransactionStatus;
import com.devstoblu.banking_system.models.CreditCard;
import com.devstoblu.banking_system.models.CreditCardTransaction;
import com.devstoblu.banking_system.repositories.CreditCardRepository;
import com.devstoblu.banking_system.repositories.CreditCardTransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CreditCardTransactionService {

    private final CreditCardRepository creditCardRepository;
    private final CreditCardTransactionRepository transactionRepository;

    public CreditCardTransactionService(CreditCardRepository creditCardRepository,
                                        CreditCardTransactionRepository transactionRepository) {
        this.creditCardRepository = creditCardRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<CreditCardTransaction> listByCardId(Long creditCardId) {
        CreditCard card = creditCardRepository.findById(creditCardId)
                .orElseThrow(() -> new EntityNotFoundException("Cartão não encontrado"));
        return transactionRepository.findByCreditCardOrderByCreatedAtDesc(card);
    }

    @Transactional
    public CreditCardTransaction authorizePurchase(Long creditCardId,
                                                   BigDecimal amount,
                                                   String merchantName,
                                                   String mcc,
                                                   String category,
                                                   Integer installmentsTotal) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da compra deve ser positivo.");
        }

        CreditCard card = creditCardRepository.findById(creditCardId)
                .orElseThrow(() -> new EntityNotFoundException("Cartão não encontrado"));

        if (!Boolean.TRUE.equals(card.getActive())) {
            throw new IllegalStateException("Cartão inativo");
        }

        if (card.getAvailableLimit().compareTo(amount) < 0) {
            throw new IllegalStateException("Limite insuficiente");
        }

        // Debita o limite disponível
        card.setAvailableLimit(card.getAvailableLimit().subtract(amount));
        creditCardRepository.save(card);

        CreditCardTransaction tx = new CreditCardTransaction();
        tx.setCreditCard(card);
        tx.setAmount(amount);
        tx.setMerchantName(merchantName);
        tx.setMcc(mcc);
        tx.setCategory(category);
        tx.setInstallmentsTotal(installmentsTotal);
        tx.setStatus(CreditCardTransactionStatus.AUTHORIZED);

        return transactionRepository.save(tx);
    }
}


