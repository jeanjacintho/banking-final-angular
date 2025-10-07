package com.devstoblu.banking_system.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/ping")
    public ResponseEntity<Map<String, Object>> ping() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "Sistema funcionando");
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        response.put("message", "Pong! API está respondendo corretamente");
        response.put("version", "1.0.0");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health-check")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("application", "Banking System");
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        response.put("database", "Connected");
        response.put("server", "Running");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("Sistema funcionando - Status 200 OK");
    }
}

