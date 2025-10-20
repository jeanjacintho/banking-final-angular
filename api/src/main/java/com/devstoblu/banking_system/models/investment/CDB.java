package com.devstoblu.banking_system.models.investment;

import com.devstoblu.banking_system.models.banking_account.Account;

import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
public class CDB extends Investment {

  private static final Logger logger = LoggerFactory.getLogger(CDB.class);

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long Id;

  public CDB() {
  }

  public CDB(double investmentTermChosen, double investmentValue) {
    setInvestmentTerm(investmentTermChosen * 12); // Contagem em meses
    setCurrentTerm(investmentTermChosen);
    // Rendimento bonus de 10% ao ano. % final calculada em cima do CDI atual
    setYield(getCDI() * (1 + (0.1 * investmentTermChosen)));
    setInvestmentValue(investmentValue);
  }

  @Override
  public void applyInvestment(Account account) {
    double term = getCurrentTerm();

    if (term <= 0) {
      double totalReturn = (getYield() + 1) * getInvestmentValue();
      account.setBalance(totalReturn + account.getBalance());
      setActive(false);
      setInvestmentReturn(getYield() * getInvestmentValue());
    } else {
      setCurrentTerm(term - 1);

      if (getCurrentTerm() <= 0) {
        double totalReturn = (getYield() + 1) * getInvestmentValue();
        account.setBalance(totalReturn + account.getBalance());
        setActive(false);
        setInvestmentReturn(getYield() * getInvestmentValue());
      }
    }
  }
}
