package com.devstoblu.banking_system.services;

import com.devstoblu.banking_system.models.Marca;
import com.devstoblu.banking_system.repositories.MarcaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MarcaService {

  private final MarcaRepository repository;

  public MarcaService(MarcaRepository repository) {
    this.repository = repository;
  }

  public List<Marca> findAll() {
    return repository.findAll();
  }

  public Marca create(Marca marca) {
    return repository.save(marca);
  }
}
