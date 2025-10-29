package com.devstoblu.banking_system.dto.loan;


import com.devstoblu.banking_system.enums.loans.LoanType;

import java.math.BigDecimal;

public class LoanSimulationDTO {
    private BigDecimal requestedAmount;
    private BigDecimal totalAmount;
    private BigDecimal monthlyInstallment;
    private BigDecimal totalInterest;
    private BigDecimal monthlyRate;
    private Integer numberOfInstallments;
    private LoanType loanType;

    public LoanSimulationDTO() {}

    public LoanSimulationDTO(BigDecimal requestedAmount, BigDecimal totalAmount, BigDecimal monthlyInstallment,
                             BigDecimal totalInterest, BigDecimal monthlyRate,
                             Integer numberOfInstallments, LoanType loanType) {
        this.requestedAmount = requestedAmount;
        this.totalAmount = totalAmount;
        this.monthlyInstallment = monthlyInstallment;
        this.totalInterest = totalInterest;
        this.monthlyRate = monthlyRate;
        this.numberOfInstallments = numberOfInstallments;
        this.loanType = loanType;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getMonthlyInstallment() {
        return monthlyInstallment;
    }

    public void setMonthlyInstallment(BigDecimal monthlyInstallment) {
        this.monthlyInstallment = monthlyInstallment;
    }

    public BigDecimal getTotalInterest() {
        return totalInterest;
    }

    public void setTotalInterest(BigDecimal totalInterest) {
        this.totalInterest = totalInterest;
    }

    public BigDecimal getMonthlyRate() {
        return monthlyRate;
    }

    public void setMonthlyRate(BigDecimal monthlyRate) {
        this.monthlyRate = monthlyRate;
    }

    public Integer getNumberOfInstallments() {
        return numberOfInstallments;
    }

    public void setNumberOfInstallments(Integer numberOfInstallments) {
        this.numberOfInstallments = numberOfInstallments;
    }

    public LoanType getLoanType() {
        return loanType;
    }

    public void setLoanType(LoanType loanType) {
        this.loanType = loanType;
    }
}
