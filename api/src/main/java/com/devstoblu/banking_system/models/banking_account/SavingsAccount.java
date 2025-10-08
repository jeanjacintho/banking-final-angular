package com.devstoblu.banking_system.models.banking_account;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("SAVINGS")
public class SavingsAccount extends Account {
  private static final double INTEREST_RATE = 0.005;
  private static final int FREE_WITHDRAWS = 4;
  private static final int FREE_TRANSFERS = 3;

  private int withdrawCount = 0;
  private int transferCount = 0;

  @Override
  public void withdraw(Double value) {
    double currentBalance = getBalance();

    if (currentBalance >= value && withdrawCount <= FREE_WITHDRAWS) {
      setBalance(currentBalance - value);
      withdrawCount++;
    } else {
      throw new RuntimeException("Saldo insuficiente ou limite de saques realizados.");
    }
  }

  // Reseta os withdraw do mes e aplica o rendimento da poupança
  @Override
  public void applyFeesAndMaintenance() {
    deposit(getBalance() * INTEREST_RATE);
    withdrawCount = 0;
    transferCount = 0;
  }

  public int getWithdrawCount() {
    return withdrawCount;
  }

  public void setWithdrawCount(int withdrawCount) {
    this.withdrawCount = withdrawCount;
  }

  public int getTransferCount() {
    return transferCount;
  }

  public void setTransferCount(int transferCount) {
    this.transferCount = transferCount;
  }
}
