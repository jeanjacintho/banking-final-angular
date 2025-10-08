package com.devstoblu.banking_system.models.banking_account;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Random;

@Entity
@DiscriminatorValue("CHECKING")
public class CheckingAccount extends Account {

  private static final double MAINTENANCE_FEE = 15.0;
  private static final double OVERDRAFT_LIMIT = 1000.0;
  private static final double OVERDRAFT_INTEREST = 0.08;

  @Override
  public void withdraw(Double value) {
    double currentBalance = getBalance();

    if ((currentBalance - value) >= (- OVERDRAFT_LIMIT)) {
      setBalance(currentBalance - value);
    } else {
      throw new RuntimeException("Saldo insuficiente ou limite excedido");
    }
  }

  // Aplica taxa de manutenção e tiver no cheque especial, aplica o juros
  @Override
  public void applyFeesAndMaintenance() {
    setBalance(getBalance() - MAINTENANCE_FEE);

    // Aplicar juros do cheque especial
    if (getBalance() < 0) {
      setBalance(getBalance() * (1 + OVERDRAFT_INTEREST));
    }
  }
}