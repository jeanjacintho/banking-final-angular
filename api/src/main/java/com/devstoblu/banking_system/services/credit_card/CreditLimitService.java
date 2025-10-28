package com.devstoblu.banking_system.services.credit_card;

import com.devstoblu.banking_system.dtos.CreditCardRequestDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CreditLimitService {
    public Double calculate(CreditCardRequestDTO dto, int score) {
        BigDecimal fator = new BigDecimal("0.35");
        if (score > 750) fator = new BigDecimal("0.6");
        else if (score < 500) fator = new BigDecimal("0.2");

        BigDecimal limite = dto.monthlyIncome().multiply(fator);
        BigDecimal min = new BigDecimal("500");
        BigDecimal max = new BigDecimal("20000");
        if (limite.compareTo(min) < 0) limite = min;
        if (limite.compareTo(max) > 0) limite = max;
        return limite.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}

