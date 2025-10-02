package com.devstoblu.banking_system.controllers;

import com.devstoblu.banking_system.models.Marca;
import com.devstoblu.banking_system.services.MarcaService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/marcas")
public class MarcaController {
  private final MarcaService service;

  public MarcaController(MarcaService service) {
    this.service = service;
  }

  public ResponseEntity<Marca> create(@RequestBody Marca marca) {
    return ResponseEntity.ok(service.create(marca));
  }
}
