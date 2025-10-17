package com.devstoblu.banking_system.models.investment;

import com.devstoblu.banking_system.models.banking_account.Account;
import com.devstoblu.banking_system.models.banking_account.CheckingAccount;
import jakarta.persistence.*;

@Entity
@Table(name = "investments")
public abstract class Investment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long Id;

  // CDI atual 8% a.a
  private double CDI = 0.08;

  private double yield;
  private double investmentTerm; // em anos
  private double investmentValue;
  private String liquidity;
  private double administrationFee;

  public abstract void applyInvestment(Account account);

  public long getId() {
    return Id;
  }

  public void setId(long id) {
    Id = id;
  }

  public double getCDI() {
    return CDI;
  }

  public void setCDI(double CDI) {
    this.CDI = CDI;
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

  public double getAdministrationFee() {
    return administrationFee;
  }

  public void setAdministrationFee(double administrationFee) {
    this.administrationFee = administrationFee;
  }


}
