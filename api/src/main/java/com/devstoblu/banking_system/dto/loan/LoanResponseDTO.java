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
    private BigDecimal monthlyInstallment;
    private Integer numberOfInstallments;
    private LocalDate startDate;
    private LoanType type;
    private LoanStatus status;
    private List<InstallmentDTO> installments;

    public LoanResponseDTO() {}

    public LoanResponseDTO(
            Long id,
            BigDecimal totalAmount,
            BigDecimal interestRate,
            BigDecimal monthlyInstallment,
            Integer numberOfInstallments,
            LocalDate startDate,
            LoanType type,
            LoanStatus status,
            List<InstallmentDTO> installments
    ) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.interestRate = interestRate;
        this.monthlyInstallment = monthlyInstallment;
        this.numberOfInstallments = numberOfInstallments;
        this.startDate = startDate;
        this.type = type;
        this.status = status;
        this.installments = installments;
    }

    // Getters e Setters
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

    public BigDecimal getMonthlyInstallment() {
        return monthlyInstallment;
    }

    public void setMonthlyInstallment(BigDecimal monthlyInstallment) {
        this.monthlyInstallment = monthlyInstallment;
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

    public static LoanResponseDTOBuilder builder() {
        return new LoanResponseDTOBuilder();
    }

    public static class LoanResponseDTOBuilder {
        private Long id;
        private BigDecimal totalAmount;
        private BigDecimal interestRate;
        private BigDecimal monthlyInstallment;
        private Integer numberOfInstallments;
        private LocalDate startDate;
        private LoanType type;
        private LoanStatus status;
        private List<InstallmentDTO> installments;

        public LoanResponseDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public LoanResponseDTOBuilder totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public LoanResponseDTOBuilder interestRate(BigDecimal interestRate) {
            this.interestRate = interestRate;
            return this;
        }

        public LoanResponseDTOBuilder monthlyInstallment(BigDecimal monthlyInstallment) {
            this.monthlyInstallment = monthlyInstallment;
            return this;
        }

        public LoanResponseDTOBuilder numberOfInstallments(Integer numberOfInstallments) {
            this.numberOfInstallments = numberOfInstallments;
            return this;
        }

        public LoanResponseDTOBuilder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public LoanResponseDTOBuilder type(LoanType type) {
            this.type = type;
            return this;
        }

        public LoanResponseDTOBuilder status(LoanStatus status) {
            this.status = status;
            return this;
        }

        public LoanResponseDTOBuilder installments(List<InstallmentDTO> installments) {
            this.installments = installments;
            return this;
        }

        public LoanResponseDTO build() {
            return new LoanResponseDTO(
                    id,
                    totalAmount,
                    interestRate,
                    monthlyInstallment,
                    numberOfInstallments,
                    startDate,
                    type,
                    status,
                    installments
            );
        }
    }
}
