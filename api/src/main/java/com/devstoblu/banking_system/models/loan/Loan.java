package com.devstoblu.banking_system.models.loan;

import com.devstoblu.banking_system.enums.loans.LoanStatus;
import com.devstoblu.banking_system.enums.loans.LoanType;
import com.devstoblu.banking_system.models.Usuario;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal totalAmount;

    private BigDecimal interestRate;

    private Integer numberOfInstallments;

    private LocalDate startDate = LocalDate.now();

    @Enumerated(EnumType.STRING)
    private LoanType type;

    @Enumerated(EnumType.STRING)
    private LoanStatus status = LoanStatus.EM_ANALISE;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoanInstallment> installments;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Usuario usuario;

    public Loan() {}

    public Loan(BigDecimal totalAmount, BigDecimal interestRate, Integer numberOfInstallments, LoanType type, Usuario usuario) {
        this.totalAmount = totalAmount;
        this.interestRate = interestRate;
        this.numberOfInstallments = numberOfInstallments;
        this.type = type;
        this.startDate = LocalDate.now();
        this.status = LoanStatus.EM_ANALISE;
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getNumberOfInstallments() {
        return numberOfInstallments;
    }
    public void setNumberOfInstallments(Integer numberOfInstallments) {
        this.numberOfInstallments = numberOfInstallments;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LoanType getType() {
        return type;
    }

    public void setType(LoanType type) {
        this.type = type;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public List<LoanInstallment> getInstallments() {
        return installments;
    }

    public void setInstallments(List<LoanInstallment> installments) {
        this.installments = installments;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
