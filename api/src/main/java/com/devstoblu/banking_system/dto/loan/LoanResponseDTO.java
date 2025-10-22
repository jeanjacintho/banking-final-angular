package com.devstoblu.banking_system.dto.loan;

import com.devstoblu.banking_system.enums.loans.LoanStatus;
import com.devstoblu.banking_system.enums.loans.LoanType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class LoanResponseDTO {
    private Long id;
    private BigDecimal totalAmount;
    private BigDecimal interestRate;
    private Integer numberOfInstallments;
    private LocalDate startDate;
    private LoanType type;
    private LoanStatus status;
    private List<InstallmentDTO> installments;

    public LoanResponseDTO() {}

    public LoanResponseDTO(Long id, BigDecimal totalAmount, BigDecimal interestRate, Integer numberOfInstallments,
                           LocalDate startDate, LoanType type, LoanStatus status, List<InstallmentDTO> installments) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.interestRate = interestRate;
        this.numberOfInstallments = numberOfInstallments;
        this.startDate = startDate;
        this.type = type;
        this.status = status;
        this.installments = installments;
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

    public List<InstallmentDTO> getInstallments() {
        return installments;
    }

    public void setInstallments(List<InstallmentDTO> installments) {
        this.installments = installments;
    }
}
