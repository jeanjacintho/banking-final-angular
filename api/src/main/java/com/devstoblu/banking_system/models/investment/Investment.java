package com.devstoblu.banking_system.models.investment;

import com.devstoblu.banking_system.models.banking_account.Account;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "investments")
public abstract class Investment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  // CDI atual 10% a.a
  public static final double CDI = 0.1;

  private double yield;
  private double investmentTerm;
  private double currentTerm;
  private double investmentValue;
  private String liquidity;
  private boolean active = true;
  private double investmentReturn;
  private String investmentType;

  @ManyToOne
  @JoinColumn(name = "account_id")
  @JsonIgnoreProperties("investments")
  private Account account; // referência à conta dona do investimento

  public abstract void applyInvestment(Account account, double currentCDI);

  public long getId() {
    return id;
  }

  public void setId(long id) {
    id = id;
  }

  public double getCDI() {
    return CDI;
  }

  public double getYield() {
    return yield;
  }

  public void setYield(double yield) {
    this.yield = yield;
  }

  public double getInvestmentTerm() {
    return investmentTerm;
  }

  public void setInvestmentTerm(double investmentTerm) {
    this.investmentTerm = investmentTerm;
  }

  public double getInvestmentValue() {
    return investmentValue;
  }

  public void setInvestmentValue(double investmentValue) {
    this.investmentValue = investmentValue;
  }

  public String getLiquidity() {
    return liquidity;
  }

  public void setLiquidity(String liquidity) {
    this.liquidity = liquidity;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public double getCurrentTerm() {
    return currentTerm;
  }

  public void setCurrentTerm(double currentTerm) {
    this.currentTerm = currentTerm;
  }

  public double getInvestmentReturn() {
    return investmentReturn;
  }

  public void setInvestmentReturn(double investmentReturn) {
    this.investmentReturn = investmentReturn;
  }

  public String getInvestmentType() {
    return investmentType;
  }
}
