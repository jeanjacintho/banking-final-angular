package com.devstoblu.banking_system.models.account;

import jakarta.persistence.*;

import java.util.Random;

@Entity
@Table(name = "account")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "account_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private int accountNumber;
  private String branch;
  private double maintenanceFee;
  private double balance;
  private String status;
  private int withdrawalCount;
  private int transferCount;

  // Construtor sem conta definida
  public Account() {
    this.accountNumber = new Random().nextInt(99999999);
    this.branch = "0001";
    this.status = "INATIVA";
  }

  // Construtor Savings e Checking Account
  public Account(double balance) {
    this.balance = balance;
    this.status = "ATIVA";
  }

  public int getAccountNumber() {
    return accountNumber;
  }

  public void setAccountNumber(int accountNumber) {
    this.accountNumber = accountNumber;
  }

  public String getBranch() {
    return branch;
  }

  public void setBranch(String branch) {
    this.branch = branch;
  }

  public double getMaintenanceFee() {
    return maintenanceFee;
  }

  public void setMaintenanceFee(double maintenanceFee) {
    this.maintenanceFee = maintenanceFee;
  }

  public double getBalance() {
    return balance;
  }

  public void setBalance(double balance) {
    this.balance = balance;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public int getWithdrawalCount() {
    return withdrawalCount;
  }

  public void setWithdrawalCount(int withdrawalCount) {
    this.withdrawalCount = withdrawalCount;
  }

  public int getTransferCount() {
    return transferCount;
  }

  public void setTransferCount(int transferCount) {
    this.transferCount = transferCount;
  }
}