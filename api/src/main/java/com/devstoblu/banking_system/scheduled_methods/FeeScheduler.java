package com.devstoblu.banking_system.scheduled_methods;

import com.devstoblu.banking_system.services.AccountService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class FeeScheduler {

  private static final Logger logger = LoggerFactory.getLogger(FeeScheduler.class);
  private final AccountService accountService;

  public FeeScheduler(AccountService accountService) {
    this.accountService = accountService;
  }

  // Dia 1 de cada mês às 2 horas
  @Scheduled(cron = "0 0 2 1 * ?")
  public void applyFeeWithSchedule() {
    try {
      accountService.applyFeesAndMaintenanceWithDetails();
    } catch (Exception e) {
      logger.error("Erro ao aplicar taxas via agendamento: ", e);
    }
  }
}