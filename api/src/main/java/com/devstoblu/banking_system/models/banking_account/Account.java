package com.devstoblu.banking_system.models.banking_account;

import com.devstoblu.banking_system.models.PixKey;
import com.devstoblu.banking_system.models.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.List;
import java.util.Random;

@Entity
@Table(name = "banking_account")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "account_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String accountNumber;
  private String agency = "0001";
  private double balance = 0.0;

  @ManyToOne
  @JoinColumn(name = "user_id")
  @JsonIgnoreProperties("accounts")
  private Usuario usuario;

  @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonIgnoreProperties("account")
  private List<PixKey> pixKeys;

  public Account() {
    this.accountNumber = String.valueOf(new Random().nextInt(99999999));
  }

  public String getAccountType() {
    return this.getClass().getAnnotation(DiscriminatorValue.class).value();
  }

  public abstract void applyFeesAndMaintenance();

  public void deposit(Double value) {
    if (value > 0) this.balance += value;
  }

  public void withdraw(Double value) {
    if (this.balance > value) this.balance -= value;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAccountNumber() {
    return accountNumber;
  }

  public String getAgency() {
    return agency;
  }

  public void setAgency(String agency) {
    this.agency = agency;
  }

  public double getBalance() {
    return balance;
  }

  public void setBalance(double balance) {
    this.balance = balance;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public List<PixKey> getPixKeys() {
    return pixKeys;
  }

  public void setPixKeys(List<PixKey> pixKeys) {
    this.pixKeys = pixKeys;
  }
}