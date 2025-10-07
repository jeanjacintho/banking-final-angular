package com.devstoblu.banking_system.models.banking_account;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("SAVINGS")
public class SavingsAccount extends Account {
  private static final double INTEREST_RATE = 0.005;
  private static final int FREE_WITHDRAWS = 6;
  private static final int FREE_TRANSFERS = 3;
  private static final double WITHDRAW_FEE = 5.0;
  private static final double TRANSFER_FEE = 3.0;

  private int withdrawCount = 0;
  private int transferCount = 0;

  @Override
  public void withdraw(Double value) {
    double currentBalance = getBalance();

    if (currentBalance >= value) {
      setBalance(currentBalance - value);
    } else {
      throw new RuntimeException("Saldo insuficiente");
    }
  }
}
