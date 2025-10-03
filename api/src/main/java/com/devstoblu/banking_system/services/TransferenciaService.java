package com.devstoblu.banking_system.services;

import com.devstoblu.banking_system.enums.TransferenciaStatus;
import com.devstoblu.banking_system.models.Transferencia;
import com.devstoblu.banking_system.repositories.TransferenciaRepository;
import com.devstoblu.banking_system.repositories.UsuarioRepository;
import com.devstoblu.banking_system.models.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransferenciaService {

    private final TransferenciaRepository transferenciaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Transferencia transfer(Long sourceAccountId, Long targetAccountId, BigDecimal amount, String description) {
        if (sourceAccountId.equals(targetAccountId)) {
            throw new IllegalArgumentException("Não é possível transferir para a mesma conta");
        }

        Usuario source = UsuarioRepository.findById(sourceAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Conta de origem não encontrada"));
        Usuario target = UsuarioRepository.findById(targetAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Conta de destino não encontrada"));

        if (source.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }

        // Débito e crédito
        source.setBalance(source.getBalance().subtract(amount));
        target.setBalance(target.getBalance().add(amount));

        UsuarioRepository.save(source);
        UsuarioRepository.save(target);

        Transferencia transferencia = Transferencia.builder()
                .sourceAccount(source)
                .targetAccount(target)
                .amount(amount)
                .status(TransferenciaStatus.COMPLETED)
                .description(description)
                .createdAt(LocalDateTime.now())
                .build();

        return transferenciaRepository.save(transferencia);
    }
}
