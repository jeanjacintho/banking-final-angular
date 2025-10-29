package com.devstoblu.banking_system.controllers;

import com.devstoblu.banking_system.enums.PixKeyType;
import com.devstoblu.banking_system.models.PixKey;
import com.devstoblu.banking_system.services.PixKeyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pix")
@CrossOrigin(origins = "*")
public class PixController {

    private final PixKeyService pixKeyService;

    public PixController(PixKeyService pixKeyService) {
        this.pixKeyService = pixKeyService;
    }

    @PostMapping("/keys")
    public ResponseEntity<?> registerKey(@RequestBody Map<String, Object> request) {
        try {
            Long accountId = Long.valueOf(request.get("accountId").toString());
            PixKeyType keyType = PixKeyType.valueOf(request.get("keyType").toString().toUpperCase());
            String keyValue = request.get("keyValue").toString();

            PixKey pixKey = pixKeyService.registerKey(accountId, keyType, keyValue);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "Chave PIX cadastrada com sucesso",
                "pixKey", Map.of(
                    "id", pixKey.getId(),
                    "keyType", pixKey.getKeyType(),
                    "keyValue", pixKey.getKeyValue()
                )
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Erro interno do servidor"
            ));
        }
    }

    @GetMapping("/keys")
    public ResponseEntity<?> getKeysByAccount(@RequestParam Long accountId) {
        try {
            List<PixKey> pixKeys = pixKeyService.getKeysByAccount(accountId);
            
            List<Map<String, Object>> keysData = pixKeys.stream()
                .map(key -> {
                    Map<String, Object> keyData = new HashMap<>();
                    keyData.put("id", key.getId());
                    keyData.put("keyType", key.getKeyType());
                    keyData.put("keyValue", key.getKeyValue());
                    keyData.put("createdAt", key.getCreatedAt());
                    return keyData;
                })
                .toList();

            return ResponseEntity.ok(Map.of(
                "success", true,
                "keys", keysData,
                "total", keysData.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Erro ao buscar chaves PIX"
            ));
        }
    }

    @DeleteMapping("/keys/{id}")
    public ResponseEntity<?> deleteKey(@PathVariable Long id) {
        try {
            pixKeyService.deleteKey(id);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Chave PIX removida com sucesso"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Erro interno do servidor"
            ));
        }
    }

    @PostMapping("/keys/delete")
    public ResponseEntity<?> deleteKeyByTypeAndValue(@RequestBody Map<String, Object> request) {
        try {
            PixKeyType keyType = PixKeyType.valueOf(request.get("keyType").toString().toUpperCase());
            String keyValue = request.get("keyValue").toString();

            pixKeyService.deleteKey(keyType, keyValue);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Chave PIX removida com sucesso"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Erro interno do servidor"
            ));
        }
    }

    @PostMapping("/resolve")
    public ResponseEntity<?> resolveKey(@RequestBody Map<String, Object> request) {
        try {
            PixKeyType keyType = PixKeyType.valueOf(request.get("keyType").toString().toUpperCase());
            String keyValue = request.get("keyValue").toString();

            var account = pixKeyService.resolveAccountByKey(keyType, keyValue);
            
            if (account.isPresent()) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "accountFound", true,
                    "accountNumber", account.get().getAccountNumber()
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "accountFound", false,
                    "message", "Chave PIX não encontrada"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Erro ao resolver chave PIX: " + e.getMessage()
            ));
        }
    }
}
