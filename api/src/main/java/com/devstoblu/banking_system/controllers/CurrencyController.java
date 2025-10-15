package com.devstoblu.banking_system.controllers;

import com.devstoblu.banking_system.dto.CurrencyConversionRequestDTO;
import com.devstoblu.banking_system.dto.CurrencyConversionResponseDTO;
import com.devstoblu.banking_system.services.CurrencyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/currency")
@RequiredArgsConstructor
public class CurrencyController {
    
    private final CurrencyService currencyService;
    
    @PostMapping("/convert")
    public ResponseEntity<CurrencyConversionResponseDTO> convertCurrency(@Valid @RequestBody CurrencyConversionRequestDTO request) {
        CurrencyConversionResponseDTO response = currencyService.convertCurrency(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/supported")
    public ResponseEntity<Map<String, Object>> getSupportedCurrencies() {
        List<String> currencies = currencyService.getSupportedCurrencies();
        
        Map<String, Object> response = new HashMap<>();
        response.put("supportedCurrencies", currencies);
        response.put("totalCount", currencies.size());
        
        return ResponseEntity.ok(response);
    }
}
