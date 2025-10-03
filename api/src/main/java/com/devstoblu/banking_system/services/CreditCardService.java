package com.devstoblu.banking_system.services;

import com.devstoblu.banking_system.models.CreditCard;
import com.devstoblu.banking_system.repositories.CreditCardRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CreditCardService {
    private final CreditCardRepository repository;

    public CreditCardService(CreditCardRepository repository) {
        this.repository = repository;
    }

    public List<CreditCard> getAll() {
        return repository.findAll();
    }

    public CreditCard getByCardNumber(String cardNumber) {
        return repository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new EntityNotFoundException("Cartão não encontrado"));
    }

    public CreditCard getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Cartão não encontrado"));
    }

    public CreditCard create(CreditCard creditCard) {
        return repository.save(creditCard);
    }

    public CreditCard update(Long id, CreditCard updatedCard) {
        Optional<CreditCard> creditCard = repository.findById(id);
        if (creditCard.isPresent()) {
            updatedCard.setId(id);
            return repository.save(updatedCard);
        }
        throw new EntityNotFoundException("Cartão não encontrado");
    }

}
