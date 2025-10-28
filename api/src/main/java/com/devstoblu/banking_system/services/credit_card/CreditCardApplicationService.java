package com.devstoblu.banking_system.services.credit_card;

import com.devstoblu.banking_system.dtos.CreditCardRequestDTO;
import com.devstoblu.banking_system.dtos.CreditCardRequestResponseDTO;
import com.devstoblu.banking_system.models.CreditCard;
import com.devstoblu.banking_system.repositories.CreditCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CreditCardApplicationService {
    private final RiskScoringService riskScoringService;
    private final CreditLimitService creditLimitService;
    private final CardIssuanceService cardIssuanceService;
    private final CreditCardRepository cardRepository;

    public CreditCardApplicationService(RiskScoringService riskScoringService, CreditLimitService creditLimitService, CardIssuanceService cardIssuanceService, CreditCardRepository cardRepository) {
        this.riskScoringService = riskScoringService;
        this.creditLimitService = creditLimitService;
        this.cardIssuanceService = cardIssuanceService;
        this.cardRepository = cardRepository;
    }

    @Transactional
    public CreditCardRequestResponseDTO processApplication(CreditCardRequestDTO dto) {
        if (!Boolean.TRUE.equals(dto.acceptTerms()) || !Boolean.TRUE.equals(dto.authorizationCreditConsultation())) {
            return new CreditCardRequestResponseDTO("recusado", null, null,
                    null, null, null, java.util.List.of("Consentimentos obrigatórios não marcados"), "Solicitação recusada.");
        }

        int score = riskScoringService.score(dto);
        Double limite = creditLimitService.calculate(dto, score);

        if (score < 450) {
            return new CreditCardRequestResponseDTO(
                    "recusado", null, null, null, null, null,
                    java.util.List.of("Score insuficiente"), "Solicitação recusada."
            );
        }

        String brand = "Visa";
        IssuedCard issued = cardIssuanceService.issuedCard(dto.name(), brand);

        CreditCard card = new CreditCard();
        card.setCardNumber(issued.pan());
        card.setCardHolderName(dto.name());
        card.setExpMonth(issued.expMonth());
        card.setExpYear(issued.expYear());
        card.setBrand(brand);
        card.setCreditLimit(BigDecimal.valueOf(limite));
        card.setAvailableLimit(limite);
        card.setMaskedPan(issued.maskedPan());
        card.setCvvHash(issued.cvvHash());
        card.setPanToken(issued.token());

        cardRepository.save(card);

        String venc = String.format("%02d/%04d", issued.expMonth(), issued.expYear());
        return new CreditCardRequestResponseDTO(
                "aprovado", limite, brand, issued.maskedPan(), issued.token(),
                venc, null, "Cartão aprovado."
        );
    }
}
