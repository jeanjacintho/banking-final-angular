package com.devstoblu.banking_system.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CurrencyConversionRequestDTO {
    
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal amount;
    
    @NotBlank(message = "Moeda de origem é obrigatória")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Moeda deve ter 3 letras maiúsculas (ex: USD, EUR, BRL)")
    private String fromCurrency;
    
    @NotBlank(message = "Moeda de destino é obrigatória")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Moeda deve ter 3 letras maiúsculas (ex: USD, EUR, BRL)")
    private String toCurrency;
}
