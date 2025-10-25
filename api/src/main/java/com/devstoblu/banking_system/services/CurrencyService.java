package com.devstoblu.banking_system.services;

import com.devstoblu.banking_system.dto.CurrencyConversionRequestDTO;
import com.devstoblu.banking_system.dto.CurrencyConversionResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class CurrencyService {
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    private static final List<String> SUPPORTED_CURRENCIES = Arrays.asList(
            "USD", "EUR", "BRL", "GBP", "JPY"
    );
    
    public CurrencyConversionResponseDTO convertCurrency(CurrencyConversionRequestDTO request) {
        validateCurrencies(request.getFromCurrency(), request.getToCurrency());
        
        if (request.getFromCurrency().equals(request.getToCurrency())) {
            return CurrencyConversionResponseDTO.of(
                    request.getAmount(),
                    request.getFromCurrency(),
                    request.getAmount(),
                    request.getToCurrency(),
                    BigDecimal.ONE,
                    "Mesma moeda"
            );
        }
        
        boolean isRealTime = true;
        BigDecimal exchangeRate;
        
        try {
            exchangeRate = fetchRealTimeRate(request.getFromCurrency(), request.getToCurrency());
        } catch (Exception e) {
            exchangeRate = getFallbackRate(request.getFromCurrency(), request.getToCurrency());
            isRealTime = false;
        }
        
        BigDecimal convertedAmount = request.getAmount()
                .multiply(exchangeRate)
                .setScale(2, RoundingMode.HALF_UP);
        
        return CurrencyConversionResponseDTO.of(
                request.getAmount(),
                request.getFromCurrency(),
                convertedAmount,
                request.getToCurrency(),
                exchangeRate,
                isRealTime ? "ExchangeRate-API (Real-time)" : "Banking System (Fallback)"
        );
    }
    
    public List<String> getSupportedCurrencies() {
        return SUPPORTED_CURRENCIES;
    }
    
    
    private BigDecimal fetchRealTimeRate(String fromCurrency, String toCurrency) {
        String url = String.format("https://api.exchangerate-api.com/v4/latest/%s", fromCurrency);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        
        if (response != null && response.containsKey("rates")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> rates = (Map<String, Object>) response.get("rates");
            if (rates.containsKey(toCurrency)) {
                Object rate = rates.get(toCurrency);
                if (rate instanceof Number) {
                    return BigDecimal.valueOf(((Number) rate).doubleValue());
                }
            }
        }
        
        throw new RuntimeException("Taxa não encontrada");
    }
    
    private BigDecimal getFallbackRate(String fromCurrency, String toCurrency) {
        switch (fromCurrency + "_" + toCurrency) {
            case "USD_BRL": return BigDecimal.valueOf(5.20);
            case "USD_EUR": return BigDecimal.valueOf(0.85);
            case "USD_GBP": return BigDecimal.valueOf(0.73);
            case "USD_JPY": return BigDecimal.valueOf(110.0);
            
            case "BRL_USD": return BigDecimal.valueOf(0.19);
            case "BRL_EUR": return BigDecimal.valueOf(0.16);
            case "BRL_GBP": return BigDecimal.valueOf(0.14);
            case "BRL_JPY": return BigDecimal.valueOf(21.0);
            
            case "EUR_USD": return BigDecimal.valueOf(1.18);
            case "EUR_BRL": return BigDecimal.valueOf(6.12);
            case "EUR_GBP": return BigDecimal.valueOf(0.86);
            case "EUR_JPY": return BigDecimal.valueOf(129.0);
            
            case "GBP_USD": return BigDecimal.valueOf(1.37);
            case "GBP_BRL": return BigDecimal.valueOf(7.12);
            case "GBP_EUR": return BigDecimal.valueOf(1.16);
            case "GBP_JPY": return BigDecimal.valueOf(150.0);
            
            case "JPY_USD": return BigDecimal.valueOf(0.0091);
            case "JPY_BRL": return BigDecimal.valueOf(0.048);
            case "JPY_EUR": return BigDecimal.valueOf(0.0078);
            case "JPY_GBP": return BigDecimal.valueOf(0.0067);
            
            default: throw new IllegalArgumentException("Conversão não suportada: " + fromCurrency + " -> " + toCurrency);
        }
    }
    
    private void validateCurrencies(String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            throw new IllegalArgumentException("Moeda de origem e destino não podem ser iguais");
        }
        
        if (!SUPPORTED_CURRENCIES.contains(fromCurrency)) {
            throw new IllegalArgumentException("Moeda de origem não suportada: " + fromCurrency);
        }
        
        if (!SUPPORTED_CURRENCIES.contains(toCurrency)) {
            throw new IllegalArgumentException("Moeda de destino não suportada: " + toCurrency);
        }
    }
}
