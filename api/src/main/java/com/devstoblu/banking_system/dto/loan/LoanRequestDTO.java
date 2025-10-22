package com.devstoblu.banking_system.dto.loan;

import com.devstoblu.banking_system.enums.loans.LoanType;
import java.math.BigDecimal;

public class LoanRequestDTO {
    private BigDecimal totalAmount;
    private BigDecimal interestRate;
    private Integer numberOfInstallments;
    private LoanType type;

    private Long userId;

    public LoanRequestDTO() {}

    public LoanRequestDTO(BigDecimal totalAmount, BigDecimal interestRate, Integer numberOfInstallments, LoanType type) {
        this.totalAmount = totalAmount;
        this.interestRate = interestRate;
        this.numberOfInstallments = numberOfInstallments;
        this.type = type;
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

    public LoanType getType() {
        return type;
    }

    public void setType(LoanType type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
