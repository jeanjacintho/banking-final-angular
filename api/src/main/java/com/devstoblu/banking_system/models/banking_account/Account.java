package com.devstoblu.banking_system.models.banking_account;

import com.devstoblu.banking_system.enums.AccountType;
import com.devstoblu.banking_system.models.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.Random;

@Entity
@Table(name = "banking_account")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "account_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private int accountNumber;
  private String agency = "0001";
  private double balance = 0.0;

  //@Enumerated(EnumType.STRING)
  //@Column(name = "account_type")
  //private AccountType accountType;

  @ManyToOne
  @JoinColumn(name = "user_id")
  @JsonIgnoreProperties("accounts")
  private Usuario usuario;

  public Account() {
    this.accountNumber = new Random().nextInt(99999999);
  }

  public String getAccountType() {
    return this.getClass().getAnnotation(DiscriminatorValue.class).value();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public int getAccountNumber() {
    return accountNumber;
  }

  public void setAccountNumber(int accountNumber) {
    this.accountNumber = accountNumber;
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
}