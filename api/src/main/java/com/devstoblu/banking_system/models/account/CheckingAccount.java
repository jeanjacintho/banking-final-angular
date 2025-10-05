package com.devstoblu.banking_system.models.account;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("CHECKING")
public class CheckingAccount extends Account {

  private double overdraftLimit;
  private double overdraftInterest;

  public CheckingAccount() {
    super();
  }

  public CheckingAccount(double balance) {
    super(balance);
    this.overdraftLimit = 1000;
    this.overdraftInterest = 0.08;

    setMaintenanceFee(15.00); // 15 reais por mes
    setWithdrawalCount(30); // 30 saques por mes
    setTransferCount(15); // 30 transferencias por mes
  }

  public double getOverdraftLimit() {
    return overdraftLimit;
  }

  public void setOverdraftLimit(double overdraftLimit) {
    this.overdraftLimit = overdraftLimit;
  }

  public double getOverdraftInterest() {
    return overdraftInterest;
  }

  public void setOverdraftInterest(double overdraftInterest) {
    this.overdraftInterest = overdraftInterest;
  }
}