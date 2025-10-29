package com.devstoblu.banking_system.models;

import com.devstoblu.banking_system.enums.CreditCardTransactionStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "credit_card_transactions")
public class CreditCardTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "credit_card_id")
    private CreditCard creditCard;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency = "BRL";

    @Column(nullable = false, length = 255)
    private String merchantName;

    @Column(length = 4)
    private String mcc;

    @Column(length = 64)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CreditCardTransactionStatus status = CreditCardTransactionStatus.AUTHORIZED;

    @Column(name = "installments_total")
    private Integer installmentsTotal;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime postedAt;

    public Long getId() {
        return id;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public CreditCardTransactionStatus getStatus() {
        return status;
    }

    public void setStatus(CreditCardTransactionStatus status) {
        this.status = status;
    }

    public Integer getInstallmentsTotal() {
        return installmentsTotal;
    }

    public void setInstallmentsTotal(Integer installmentsTotal) {
        this.installmentsTotal = installmentsTotal;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(LocalDateTime postedAt) {
        this.postedAt = postedAt;
    }
}



