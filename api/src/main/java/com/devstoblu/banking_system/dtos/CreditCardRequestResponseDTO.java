package com.devstoblu.banking_system.dtos;

import java.math.BigDecimal;
import java.util.List;

public record CreditCardRequestResponseDTO (
        String statusSolicitacao,
        BigDecimal limiteAprovado,
        String brand,
        String maskedPan,
        String cardToken,
        String dataVencimentoFatura,
        List<String> pendencias,
        String mensagem
) {
}
