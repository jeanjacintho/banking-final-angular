package com.devstoblu.banking_system.repositories.loan;

import com.devstoblu.banking_system.enums.loans.LoanStatus;
import com.devstoblu.banking_system.models.Usuario;
import com.devstoblu.banking_system.models.loan.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface  LoanRepository extends JpaRepository<Loan, Long> {

    boolean existsByUsuarioAndStatus(Usuario usuario, LoanStatus status);

    boolean existsByUsuarioAndStatusAndDueDateBefore(Usuario usuario, LoanStatus status, LocalDate date);

    List<Loan> findByUsuarioId(Long userId);
}
