package com.devstoblu.banking_system.models.investment;

import com.devstoblu.banking_system.models.banking_account.Account;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.ArrayList;
import java.util.List;

@Entity
public class RendaFixa extends Investment {

  @Id
  @GeneratedValue
  private long id;

  private static final double ADMINISTRATION_FEE = 0.005 / 12;

  @ElementCollection
  private List<Double> cdiEachMonth = new ArrayList<>();

  public RendaFixa() {
  }

  public RendaFixa(double invetmentValue) {
    setInvestmentValue(invetmentValue);
    setYield(getCDI() * 0.9);
  }

  // Calcula o CDI do mes e salva em um array
  @Override
  public void applyInvestment(Account account, double currentCdi) {
    cdiEachMonth.add((currentCdi * 0.9) / 12);
    setCurrentTerm(getCurrentTerm() + 1);
    setInvestmentTerm(getCurrentTerm());
  }

  // Ao ser solicitado a retirada do investimento, calcula o rendimento com base nos cdis mensais
  public void withdraw(Account account) {
    double total = getInvestmentValue();

    for (Double monthlyCdi : cdiEachMonth) {
      // Rendimento líquido de cada mês
      double effectiveRate = monthlyCdi - ADMINISTRATION_FEE;
      total *= (1 + effectiveRate);
    }

    // Atualiza a conta e encerra o investimento
    account.setBalance(account.getBalance() + total);
    setActive(false);

    setInvestmentReturn(total - getInvestmentValue());
  }

  public List<Double> getCdiEachMonth() {
    return cdiEachMonth;
  }

  public void setCdiEachMonth(List<Double> cdiEachMonth) {
    this.cdiEachMonth = cdiEachMonth;
  }

  @Override
  public String getInvestmentType() {
    return "RENDA_FIXA";
  }
}
