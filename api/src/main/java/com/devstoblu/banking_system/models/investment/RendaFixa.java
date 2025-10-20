package com.devstoblu.banking_system.models.investment;

import com.devstoblu.banking_system.models.banking_account.Account;
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

  List<Double> cdiEachMonth = new ArrayList<>();

  public RendaFixa() {
  }

  public RendaFixa(double invetmentValue) {
    setInvestmentValue(invetmentValue);
  }

  // Calcula o CDI do mes e salva em um array
  @Override
  public void applyInvestment(Account account, double currentCdi) {
    cdiEachMonth.add((currentCdi * 0.9) / 12);
  }

  // Calcula a média dos valor de cada mes do cdi
  public double averageCdi() {
    double sum = 0;
    for (Double eachCdi : cdiEachMonth) {
      sum += eachCdi;
    }
    return sum / cdiEachMonth.toArray().length;
  }

  // Ao ser solicitado a retirada do investimento, calcula o rendimento com base na media do cdis
  public void withdraw(Account account) {
    double totalReturn = getInvestmentValue() * (1 + (averageCdi() - getAdministrationFee() / 12));
    account.setBalance(totalReturn + account.getBalance());
    setActive(false);
    setInvestmentReturn(getInvestmentValue() * averageCdi());
  }
}
