package com.devstoblu.banking_system.models.investment;

import com.devstoblu.banking_system.models.Usuario;
import com.devstoblu.banking_system.models.banking_account.Account;
import com.devstoblu.banking_system.models.banking_account.CheckingAccount;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
public class CDB extends Investment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long Id;

  @ManyToOne
  @JoinColumn(name = "account_id")
  @JsonIgnoreProperties("investments")
  private Account account; // referência à conta dona do investimento

  public CDB() {
  }

  public CDB(double investmentTermChosen, double investmentValue) {
    setInvestmentTerm(investmentTermChosen); //* 12
    // Rendimento bonus de 10% ao ano. % final calculada em cima do CDI atual
    setYield(getCDI() * (1 + (0.1 * investmentTermChosen)));
    setInvestmentValue(investmentValue);
  }

  @Override
  public void applyInvestment(Account account) {
    double term = getInvestmentTerm();
    if (term == 0) {
      double valueAndFee = (getYield() + 1) * getInvestmentValue();
      account.setBalance(valueAndFee + account.getBalance());
    } else {
      setInvestmentTerm(term - 1);
    }
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }
}
