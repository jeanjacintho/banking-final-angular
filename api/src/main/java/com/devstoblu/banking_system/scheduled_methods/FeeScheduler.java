package com.devstoblu.banking_system.scheduled_methods;

import com.devstoblu.banking_system.models.investment.Investment;
import com.devstoblu.banking_system.services.AccountService;
import com.devstoblu.banking_system.services.InvestmentService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class FeeScheduler {

  private static final Logger logger = LoggerFactory.getLogger(FeeScheduler.class);
  private final AccountService accountService;
  private final InvestmentService investmentService;

  public FeeScheduler(AccountService accountService, InvestmentService investmentService) {
    this.accountService = accountService;
    this.investmentService = investmentService;
  }

  // Dia 1 de cada mês as 3 horas e 1 minuto no GMT, aqui é 00:01
  @Scheduled(cron = "0 1 3 1 * ?")
  public void applyFeeWithScheduled() {
    try {
      accountService.applyFeesAndMaintenanceWithDetails();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  // Dia 1 de cada mês as 3 horas e 5 minutos da madrugrada no GMT, aqui é 00:05
  @Scheduled(cron = "0 5 3 1 * ?")
  public void applyInvestmentScheduled() {
    try {
      investmentService.applyInvestmentForAllAccounts(Investment.CDI);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}