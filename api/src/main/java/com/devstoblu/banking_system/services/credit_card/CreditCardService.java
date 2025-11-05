package com.devstoblu.banking_system.services.credit_card;

import com.devstoblu.banking_system.models.CreditCard;
import com.devstoblu.banking_system.models.Usuario;
import com.devstoblu.banking_system.repositories.CreditCardRepository;
import com.devstoblu.banking_system.services.EncryptionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CreditCardService {
    private final CreditCardRepository repository;
    private final EncryptionService encryptionService;

    public CreditCardService(CreditCardRepository repository, EncryptionService encryptionService) {
        this.repository = repository;
        this.encryptionService = encryptionService;
    }

    public List<CreditCard> getAll() {
        return repository.findAll();
    }

    public List<CreditCard> getByUser(Usuario user) {
        return repository.findByUsuario(user);
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

    public String delete(Long id) {
        Optional<CreditCard> card = repository.findById(id);
        if (card.isEmpty()) throw new EntityNotFoundException("Cartão não encontrado");
        repository.delete(card.get());
        return "Cartão excluído com sucesso!";
    }

    public String getDecryptedCvv(Long id) {
        CreditCard card = getById(id);
        if (card.getCvvEncrypted() == null || card.getCvvEncrypted().isEmpty()) {
            throw new IllegalStateException("CVV não disponível para este cartão");
        }
        return encryptionService.decrypt(card.getCvvEncrypted());
    }

}
