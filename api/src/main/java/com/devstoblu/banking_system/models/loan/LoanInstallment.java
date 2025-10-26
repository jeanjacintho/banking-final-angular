package com.devstoblu.banking_system.models.loan;

import com.devstoblu.banking_system.enums.loans.InstallmentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "loan_installments")
public class LoanInstallment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer number;

    private LocalDate dueDate;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private InstallmentStatus status;

    @ManyToOne
    @JoinColumn(name = "loan_id")
    private Loan loan;

    public LoanInstallment() {
        this.status = InstallmentStatus.PENDENTE;
    }

    public LoanInstallment(Integer number, LocalDate dueDate, BigDecimal amount, Loan loan) {
        if (number <= 0)
            throw new IllegalArgumentException("O número da parcela deve ser maior que zero.");
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("O valor da parcela deve ser maior que zero.");
        this.number = number;
        this.dueDate = dueDate;
        this.amount = amount;
        this.loan = loan;
        this.status = InstallmentStatus.PENDENTE;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }
    public void setNumber(Integer number) {
        this.number = number;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public InstallmentStatus getStatus() {
        return status;
    }

    public void setStatus(InstallmentStatus status) {
        this.status = status;
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }
}
