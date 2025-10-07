package com.devstoblu.banking_system.services.credit_card;

import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class CardIssuanceService {
    private final SecureRandom random = new SecureRandom();

    private int luhnCheckDigit(String number) {
        int sum = 0; boolean alt = true;
        for (int i = number.length() - 1; i >= 0; i--) {
            int n = number.charAt(i) - '0';
            if (alt) { n *= 2; if (n > 9) n -= 9; }
            sum += n; alt = !alt;
        }
        return (10 - (sum % 10)) % 10;
    }

    private String digits(int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(random.nextInt(10));
        return sb.toString();
    }

    private String generateLuhnPan(String brand) {
        // Visa (prefixo 4) para exemplo
        String bin = "4" + digits(5);
        String body = digits(9);         // 15 dígitos antes do verificador
        String partial = bin + body;
        int check = luhnCheckDigit(partial);
        return partial + check;
    }

    private String mask(String pan) { return "**** **** **** " + pan.substring(pan.length() - 4); }
    private String generateCvv() { return String.format("%03d", random.nextInt(1000)); }
    private int[] expiryPlusYears(int years) {
        LocalDate d = LocalDate.now().plusYears(years);
        return new int[]{ d.getMonthValue(), d.getYear() };
    }

    public IssuedCard issuedCard(String holder, String brand) {
        String pan = generateLuhnPan(brand);
        String masked = mask(pan);
        String token = UUID.randomUUID().toString();
        String cvv = generateCvv();
        String cvvHash = DigestUtils.md5DigestAsHex(cvv.getBytes());
        int[] exp = expiryPlusYears(3);
        return new IssuedCard(brand, masked, token, pan, cvvHash, holder, exp[0], exp[1]);
    }
}
