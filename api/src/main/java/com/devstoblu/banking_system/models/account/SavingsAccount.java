package com.devstoblu.banking_system.models.account;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("SAVINGS")
public class SavingsAccount extends Account {

  private double income;

  public SavingsAccount() {
    super();
  }

  public SavingsAccount(double balance) {
    super(balance);
    this.income = 0.005;

    setMaintenanceFee(0); // sem taxa de manutencao
    setWithdrawalCount(6); // 6 saques por mes
    setTransferCount(3); // 3 transferencias por mes
  }
}
