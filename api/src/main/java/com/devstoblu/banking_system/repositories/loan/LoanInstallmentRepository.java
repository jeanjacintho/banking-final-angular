package com.devstoblu.banking_system.repositories.loan;

import com.devstoblu.banking_system.models.loan.LoanInstallment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {

}
