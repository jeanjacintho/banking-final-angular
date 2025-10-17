package com.devstoblu.banking_system.services;

import com.devstoblu.banking_system.models.banking_account.Account;
import com.devstoblu.banking_system.models.banking_account.CheckingAccount;
import com.devstoblu.banking_system.models.investment.CDB;
import com.devstoblu.banking_system.models.investment.Investment;
import com.devstoblu.banking_system.repositories.AccountRepository;
import com.devstoblu.banking_system.repositories.InvestmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvestmentService {
  private final AccountRepository accountRepository;

  private final InvestmentRepository investmentRepository;

  public InvestmentService(AccountRepository accountRepository, InvestmentRepository investmentRepository) {
    this.accountRepository = accountRepository;
    this.investmentRepository = investmentRepository;
  }

  public CDB createInvestment(String accountNumber, double term, double value) {
    Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new RuntimeException("Conta não encontrada"));
    account.withdraw(value);

    CDB cdb = new CDB(term, value);
    cdb.setAccount(account);

    account.getInvestments().add(cdb);
    accountRepository.save(account);

    return cdb;
  }

  public void deleteInvestment(String accountNumber, Long id) {
    Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
    Investment investment = investmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Investimento não encontrado"));

    investmentRepository.delete(investment);
  }

  public void applyInvestimento(String accountNumber) {
    List<Investment> investments = investmentRepository.findAll();
    Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

    for (Investment i : investments) {
      i.applyInvestment(account);
      if (i.getInvestmentTerm() == 0) {
        deleteInvestment(accountNumber, i.getId());
      }
      investmentRepository.save(i);
    }
  }
}
