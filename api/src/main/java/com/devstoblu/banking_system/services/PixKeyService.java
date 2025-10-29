package com.devstoblu.banking_system.services;

import com.devstoblu.banking_system.enums.PixKeyType;
import com.devstoblu.banking_system.models.PixKey;
import com.devstoblu.banking_system.models.banking_account.Account;
import com.devstoblu.banking_system.repositories.AccountRepository;
import com.devstoblu.banking_system.repositories.PixKeyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Transactional
public class PixKeyService {
    private final PixKeyRepository pixKeyRepository;
    private final AccountRepository accountRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\+?[1-9]\\d{1,14}$"
    );

    public PixKeyService(PixKeyRepository pixKeyRepository, AccountRepository accountRepository) {
        this.pixKeyRepository = pixKeyRepository;
        this.accountRepository = accountRepository;
    }

    public PixKey registerKey(Long accountId, PixKeyType keyType, String keyValue) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        String normalizedValue = normalizeKeyValue(keyType, keyValue);
        validateKeyValue(keyType, normalizedValue);

        if (pixKeyRepository.existsByKeyTypeAndKeyValue(keyType, normalizedValue)) {
            throw new RuntimeException("Chave PIX já está em uso por outra conta");
        }

        long keysCount = pixKeyRepository.countKeysByAccountId(accountId);
        if (keysCount >= 3) {
            throw new RuntimeException("Limite máximo de 3 chaves PIX por conta");
        }

        PixKey pixKey = new PixKey(keyType, normalizedValue, account);
        return pixKeyRepository.save(pixKey);
    }

    public void deleteKey(Long keyId) {
        PixKey pixKey = pixKeyRepository.findById(keyId)
            .orElseThrow(() -> new RuntimeException("Chave PIX não encontrada"));
        
        pixKeyRepository.delete(pixKey);
    }

    public void deleteKey(PixKeyType keyType, String keyValue) {
        String normalizedValue = normalizeKeyValue(keyType, keyValue);
        PixKey pixKey = pixKeyRepository.findByKeyTypeAndKeyValue(keyType, normalizedValue)
            .orElseThrow(() -> new RuntimeException("Chave PIX não encontrada"));
        
        pixKeyRepository.delete(pixKey);
    }

    public Optional<Account> resolveAccountByKey(PixKeyType keyType, String keyValue) {
        String normalizedValue = normalizeKeyValue(keyType, keyValue);
        return pixKeyRepository.findByKeyTypeAndKeyValue(keyType, normalizedValue)
            .map(PixKey::getAccount);
    }

    public List<PixKey> getKeysByAccount(Long accountId) {
        return pixKeyRepository.findByAccountId(accountId);
    }

    private String normalizeKeyValue(PixKeyType keyType, String keyValue) {
        if (keyValue == null || keyValue.trim().isEmpty()) {
            throw new RuntimeException("Valor da chave não pode ser vazio");
        }

        return switch (keyType) {
            case CPF -> normalizeCpf(keyValue);
            case EMAIL -> normalizeEmail(keyValue);
            case PHONE -> normalizePhone(keyValue);
        };
    }

    private String normalizeCpf(String cpf) {
        String digitsOnly = cpf.replaceAll("[^0-9]", "");
        if (digitsOnly.length() != 11) {
            throw new RuntimeException("CPF deve conter 11 dígitos");
        }
        return digitsOnly;
    }

    private String normalizeEmail(String email) {
        String normalized = email.toLowerCase().trim();
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new RuntimeException("Formato de email inválido");
        }
        return normalized;
    }

    private String normalizePhone(String phone) {
        String digitsOnly = phone.replaceAll("[^0-9+]", "");
        if (!PHONE_PATTERN.matcher(digitsOnly).matches()) {
            throw new RuntimeException("Formato de telefone inválido");
        }
        return digitsOnly;
    }

    private void validateKeyValue(PixKeyType keyType, String normalizedValue) {
        switch (keyType) {
            case CPF -> validateCpf(normalizedValue);
            case EMAIL -> validateEmail(normalizedValue);
            case PHONE -> validatePhone(normalizedValue);
        }
    }

    private void validateCpf(String cpf) {
        if (cpf.length() != 11) {
            throw new RuntimeException("CPF deve conter 11 dígitos");
        }
        
        if (cpf.matches("(\\d)\\1{10}")) {
            throw new RuntimeException("CPF inválido");
        }

        if (!isValidCpf(cpf)) {
            throw new RuntimeException("CPF inválido");
        }
    }

    private void validateEmail(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new RuntimeException("Formato de email inválido");
        }
    }

    private void validatePhone(String phone) {
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new RuntimeException("Formato de telefone inválido");
        }
    }

    private boolean isValidCpf(String cpf) {
        if (cpf.length() != 11) return false;

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int firstDigit = 11 - (sum % 11);
        if (firstDigit >= 10) firstDigit = 0;

        if (Character.getNumericValue(cpf.charAt(9)) != firstDigit) return false;

        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        int secondDigit = 11 - (sum % 11);
        if (secondDigit >= 10) secondDigit = 0;

        return Character.getNumericValue(cpf.charAt(10)) == secondDigit;
    }
}
