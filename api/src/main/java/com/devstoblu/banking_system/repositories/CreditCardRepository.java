package com.devstoblu.banking_system.repositories;

import com.devstoblu.banking_system.models.CreditCard;
import com.devstoblu.banking_system.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {

    Optional<CreditCard> findByCardNumber(String number);
    
    List<CreditCard> findByUsuario(Usuario usuario);
}
