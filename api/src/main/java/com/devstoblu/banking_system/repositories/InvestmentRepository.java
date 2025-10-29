package com.devstoblu.banking_system.repositories;

import com.devstoblu.banking_system.models.investment.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {

  // Busca todos os investimentos ativos de uma conta
  List<Investment> findByAccount_AccountNumberAndActiveTrue(String accountNumber);

  // Busca todos os investimentos ativos ou não de uma conta
  List<Investment> findByAccount_AccountNumber(String accountNumber);

  // Busca todos os investimentos por usuário
  List<Investment> findByAccount_Usuario_Id(Long userId);
}
