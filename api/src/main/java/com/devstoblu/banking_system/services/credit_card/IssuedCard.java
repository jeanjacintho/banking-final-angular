package com.devstoblu.banking_system.services.credit_card;

public record IssuedCard (String brand, String maskedPan, String token, String pan, String cvv, String cvvHash, String cvvEncrypted,
                          String holder, int expMonth, int expYear) {}
