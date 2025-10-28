package com.devstoblu.banking_system.repositories.loan;

import com.devstoblu.banking_system.enums.loans.LoanStatus;
import com.devstoblu.banking_system.models.Usuario;
import com.devstoblu.banking_system.models.loan.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface  LoanRepository extends JpaRepository<Loan, Long> {

    boolean existsByUsuarioAndStatus(Usuario usuario, LoanStatus status);

    @Query("SELECT COUNT(l) > 0 FROM Loan l " +
           "JOIN l.installments i " +
           "WHERE l.usuario = :usuario " +
           "AND l.status = :status " +
           "AND i.dueDate < :date " +
           "AND i.status = com.devstoblu.banking_system.enums.loans.InstallmentStatus.PENDENTE")
    boolean existsByUsuarioAndStatusAndDueDateBefore(
            @Param("usuario") Usuario usuario,
            @Param("status") LoanStatus status,
            @Param("date") LocalDate date
    );

    List<Loan> findByUsuarioId(Long userId);
}
