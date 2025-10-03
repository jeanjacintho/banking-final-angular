package com.devstoblu.banking_system.models;

import com.devstoblu.banking_system.enums.TransferenciaStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "source_account_id", nullable = false)
    private Usuario sourceAccount; // Quem manda

    @ManyToOne
    @JoinColumn(name = "target_account_id", nullable = false)
    private Usuario targetAccount; // Quem recebe

    @Column(nullable = false)
    private BigDecimal amount; // montante

    @Column(nullable = false)
    private LocalDateTime createdAt; //data da transferencia

    @Enumerated(EnumType.STRING)
    private TransferenciaStatus status; // Status da transferencia

    private String description;
}
