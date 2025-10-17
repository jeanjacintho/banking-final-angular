package com.devstoblu.banking_system.repositories;

import com.devstoblu.banking_system.models.investment.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {
}
