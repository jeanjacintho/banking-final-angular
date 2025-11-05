package com.devstoblu.banking_system.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class EncryptionService {
    
    @Value("${app.encryption.key:bankr-secret-key-32-bytes-long!!}")
    private String encryptionKey;
    
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    
    private SecretKeySpec getSecretKey() {
        // Garantir que a chave tenha exatamente 16 bytes (128 bits) para AES-128
        byte[] keyBytes = encryptionKey.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length != 16) {
            // Ajustar para 16 bytes
            byte[] adjustedKey = new byte[16];
            System.arraycopy(keyBytes, 0, adjustedKey, 0, Math.min(keyBytes.length, 16));
            if (keyBytes.length < 16) {
                // Preencher com zeros se necessário
                for (int i = keyBytes.length; i < 16; i++) {
                    adjustedKey[i] = 0;
                }
            }
            keyBytes = adjustedKey;
        }
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }
    
    /**
     * Criptografa um valor usando AES
     */
    public String encrypt(String value) {
        try {
            SecretKeySpec secretKey = getSecretKey();
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            
            byte[] encryptedBytes = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criptografar valor", e);
        }
    }
    
    /**
     * Descriptografa um valor usando AES
     */
    public String decrypt(String encryptedValue) {
        try {
            SecretKeySpec secretKey = getSecretKey();
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedValue));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao descriptografar valor", e);
        }
    }
}

