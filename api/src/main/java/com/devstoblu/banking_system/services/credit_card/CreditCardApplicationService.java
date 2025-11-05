package com.devstoblu.banking_system.services.credit_card;

import com.devstoblu.banking_system.dtos.CreditCardRequestDTO;
import com.devstoblu.banking_system.dtos.CreditCardRequestResponseDTO;
import com.devstoblu.banking_system.models.CreditCard;
import com.devstoblu.banking_system.models.Usuario;
import com.devstoblu.banking_system.repositories.CreditCardRepository;
import com.devstoblu.banking_system.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CreditCardApplicationService {
    private final RiskScoringService riskScoringService;
    private final CreditLimitService creditLimitService;
    private final CardIssuanceService cardIssuanceService;
    private final CreditCardRepository cardRepository;
    private final UsuarioRepository usuarioRepository;

    public CreditCardApplicationService(RiskScoringService riskScoringService, CreditLimitService creditLimitService, CardIssuanceService cardIssuanceService, CreditCardRepository cardRepository, UsuarioRepository usuarioRepository) {
        this.riskScoringService = riskScoringService;
        this.creditLimitService = creditLimitService;
        this.cardIssuanceService = cardIssuanceService;
        this.cardRepository = cardRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public CreditCardRequestResponseDTO processApplication(CreditCardRequestDTO dto, Long userId) {
        if (!Boolean.TRUE.equals(dto.acceptTerms()) || !Boolean.TRUE.equals(dto.authorizationCreditConsultation())) {
            return new CreditCardRequestResponseDTO("recusado", null, null,
                    null, null, null, null, java.util.List.of("Consentimentos obrigatórios não marcados"), "Solicitação recusada.");
        }

        Usuario user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        int score = riskScoringService.score(dto);
        BigDecimal limite = creditLimitService.calculate(dto, score);

        if (score < 450) {
            return new CreditCardRequestResponseDTO(
                    "recusado", null, null, null, null, null, null,
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
        card.setCreditLimit(limite);
        card.setAvailableLimit(limite);
        card.setMaskedPan(issued.maskedPan());
        card.setCvvHash(issued.cvvHash());
        card.setCvvEncrypted(issued.cvvEncrypted());
        card.setPanToken(issued.token());
        card.setUsuario(user);

        cardRepository.save(card);

        String venc = String.format("%02d/%04d", issued.expMonth(), issued.expYear());
        return new CreditCardRequestResponseDTO(
                "aprovado", limite, brand, issued.maskedPan(), issued.token(),
                venc, issued.cvv(), java.util.List.of(), "Cartão aprovado."
        );
    }
}
