package com.devstoblu.banking_system.services;

import com.devstoblu.banking_system.models.banking_account.Account;
import com.devstoblu.banking_system.models.investment.CDB;
import com.devstoblu.banking_system.models.investment.Investment;
import com.devstoblu.banking_system.models.investment.RendaFixa;
import com.devstoblu.banking_system.repositories.AccountRepository;
import com.devstoblu.banking_system.repositories.InvestmentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class InvestmentService {
  private final AccountRepository accountRepository;

  private final InvestmentRepository investmentRepository;

  public InvestmentService(AccountRepository accountRepository, InvestmentRepository investmentRepository) {
    this.accountRepository = accountRepository;
    this.investmentRepository = investmentRepository;
  }

  public CDB createInvestmentCdb(String accountNumber, double term, double value) {
    Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));

    if (account.getAccountType().equals("SAVINGS")) {
      throw new IllegalArgumentException("Conta Poupança não pode realizar investimentos");
    }
    account.withdraw(value);

    CDB cdb = new CDB(term, value);
    cdb.setAccount(account);

    account.getInvestments().add(cdb);
    accountRepository.save(account);

    return cdb;
  }

  public RendaFixa createInvestmentRenda(String accountNumber, double value) {
    Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));

    if (account.getAccountType().equals("SAVINGS")) {
      throw new IllegalArgumentException("Conta Poupança não pode realizar investimentos");
    }
    account.withdraw(value);

    RendaFixa rendaFixa = new RendaFixa(value);
    rendaFixa.setAccount(account);

    account.getInvestments().add(rendaFixa);
    accountRepository.save(account);

    return rendaFixa;
  }

  public void deleteInvestment(String accountNumber, Long id) {
    Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));
    Investment investment = investmentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Investimento não encontrado"));

    investmentRepository.delete(investment);
  }

  public void applyInvestment(String accountNumber, double currentCdi) {
    List<Investment> investments = investmentRepository.findByAccount_AccountNumberAndActiveTrue(accountNumber);

    if (investments.isEmpty()) {
      throw new IllegalArgumentException("Nenhum investimento ativo encontrado para a conta");
    }

    Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));

    for (Investment i : investments) {
      i.applyInvestment(account, currentCdi);
      investmentRepository.save(i);
    }
    accountRepository.save(account);
  }

  public void withdrawInvestment(String accountNumber) {
    Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));

    List<Investment> investments = investmentRepository.findByAccount_AccountNumberAndActiveTrue(accountNumber);

    if (investments.isEmpty()) {
      throw new IllegalArgumentException("Nenhum investimento ativo encontrado para a conta");
    }

    for (Investment i : investments) {
      if (i instanceof RendaFixa) {
        ((RendaFixa) i).withdraw(account);
        investmentRepository.save(i);
      }
    }
    accountRepository.save(account);
  }
}
