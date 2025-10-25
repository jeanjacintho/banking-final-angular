package com.devstoblu.banking_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyConversionResponseDTO {
    
    private BigDecimal originalAmount;
    private String fromCurrency;
    private BigDecimal convertedAmount;
    private String toCurrency;
    private BigDecimal exchangeRate;
    private LocalDateTime conversionDate;
    private String provider;
    
    public static CurrencyConversionResponseDTO of(
            BigDecimal originalAmount,
            String fromCurrency,
            BigDecimal convertedAmount,
            String toCurrency,
            BigDecimal exchangeRate,
            String provider
    ) {
        return CurrencyConversionResponseDTO.builder()
                .originalAmount(originalAmount)
                .fromCurrency(fromCurrency)
                .convertedAmount(convertedAmount)
                .toCurrency(toCurrency)
                .exchangeRate(exchangeRate)
                .conversionDate(LocalDateTime.now())
                .provider(provider)
                .build();
    }
}
