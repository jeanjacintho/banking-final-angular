package com.devstoblu.banking_system.dto.loan;

import java.math.BigDecimal;

public class LoanSimulationDTO {
    private BigDecimal totalAmount;
    private BigDecimal monthlyInstallment;
    private BigDecimal totalToPay;
    private BigDecimal monthlyRate;
    private Integer numberOfInstallments;

    public LoanSimulationDTO() {}

    public LoanSimulationDTO(BigDecimal totalAmount, BigDecimal monthlyInstallment, BigDecimal totalToPay,
                             BigDecimal monthlyRate, Integer numberOfInstallments) {
        this.totalAmount = totalAmount;
        this.monthlyInstallment = monthlyInstallment;
        this.totalToPay = totalToPay;
        this.monthlyRate = monthlyRate;
        this.numberOfInstallments = numberOfInstallments;
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

    public BigDecimal getTotalToPay() {
        return totalToPay;
    }

    public void setTotalToPay(BigDecimal totalToPay) {
        this.totalToPay = totalToPay;
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

}
