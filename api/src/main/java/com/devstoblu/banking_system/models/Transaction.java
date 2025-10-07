package com.devstoblu.banking_system.models;

import com.devstoblu.banking_system.enums.TransferType;
import com.devstoblu.banking_system.models.banking_account.Account;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Transaction {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Account fromAccount;

    @ManyToOne
    private Account toAccount;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private TransferType type;

    private LocalDateTime timestamp;

    public Transaction(Long id, Account fromAccount, Account toAccount, Double amount, TransferType type, LocalDateTime timestamp) {
        this.id = id;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.type = type;
        this.timestamp = timestamp;
    }

    public Transaction() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(Account fromAccount) {
        this.fromAccount = fromAccount;
    }

    public Account getToAccount() {
        return toAccount;
    }

    public void setToAccount(Account toAccount) {
        this.toAccount = toAccount;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public TransferType getType() {
        return type;
    }

    public void setType(TransferType type) {
        this.type = type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
