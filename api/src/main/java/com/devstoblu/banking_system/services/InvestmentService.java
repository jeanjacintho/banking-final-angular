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

  public List<Investment> findAll() {
    return investmentRepository.findAll();
  }

  public List<Investment> findByUser(Long userId) {
    return investmentRepository.findByAccount_Usuario_Id(userId);
  }

  public CDB createInvestmentCdb(String accountNumber, double term, double value) {
    Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada."));

    if (account.getAccountType().equals("SAVINGS")) {
      throw new IllegalArgumentException("Conta Poupança não pode realizar investimentos.");
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
            .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada."));

    if (account.getAccountType().equals("SAVINGS")) {
      throw new IllegalArgumentException("Conta Poupança não pode realizar investimentos.");
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
            .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada."));
    Investment investment = investmentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Investimento não encontrado."));

    investmentRepository.delete(investment);
  }


  // Aplica investimento em uma conta especifica via endpoint para testes
  public void applyInvestment(String accountNumber, double currentCdi) {
    List<Investment> investments = investmentRepository.findByAccount_AccountNumberAndActiveTrue(accountNumber);

    if (investments.isEmpty()) {
      throw new IllegalArgumentException("Nenhum investimento ativo encontrado para a conta.");
    }

    Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada."));

    for (Investment i : investments) {
      i.applyInvestment(account, currentCdi);
      investmentRepository.save(i);
    }
    accountRepository.save(account);
  }

  // Aplica investimento para todas as contas automaticamente via @Scheduled
  public void applyInvestmentForAllAccounts(double currentCdi) {
    List<Account> accounts = accountRepository.findAll();

    for (Account account : accounts) {
      List<Investment> investments = investmentRepository.findByAccount_AccountNumberAndActiveTrue(account.getAccountNumber());

      for (Investment investment : investments) {
        investment.applyInvestment(account, currentCdi);
        investmentRepository.save(investment);
      }
      accountRepository.save(account);
    }
  }

  public void withdrawInvestment(String accountNumber, Long id) {
    Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada."));

    Investment investment = investmentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Investimento não encontrado."));

    if (!(investment instanceof RendaFixa)) {
      throw new IllegalArgumentException("O investimento não é Renda Fixa.");
    }

    if (investment.getCurrentTerm() == 0) {
      throw new IllegalArgumentException("Investimento na Renda Fixa deve estar aplicado pelo menos 1 mês para ser retirado.");
    }

    if (investment.getId() == id) {
      ((RendaFixa) investment).withdraw(account);
      investmentRepository.save(investment);
    }

    accountRepository.save(account);
  }
}
