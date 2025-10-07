package com.devstoblu.banking_system.services.credit_card;

import com.devstoblu.banking_system.dtos.CreditCardRequestDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class RiskScoringService {
    public int score(CreditCardRequestDTO dto) {
        int base = 500;
        if (dto.monthlyIncome().doubleValue() < 3000) base -= 80;
        if ("CLT".equalsIgnoreCase(dto.sourceIncome())) base += 40;
        if (dto.employmentTimeMonths() != null && dto.employmentTimeMonths() > 12) base += 30;
        int idade = Period.between(dto.dateOfBirth(), LocalDate.now()).getYears();
        if (idade < 21) base -= 50;
        return Math.max(300, Math.min(base, 900));
    }
}
