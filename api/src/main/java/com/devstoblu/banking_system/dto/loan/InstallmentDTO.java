package com.devstoblu.banking_system.dto.loan;

import com.devstoblu.banking_system.enums.loans.InstallmentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public class InstallmentDTO {
    private Integer number;
    private LocalDate dueDate;
    private BigDecimal amount;
    private InstallmentStatus status;

    public InstallmentDTO() {}

    public InstallmentDTO(Integer number, LocalDate dueDate, BigDecimal amount, InstallmentStatus status) {
        this.number = number;
        this.dueDate = dueDate;
        this.amount = amount;
        this.status = status;
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

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public InstallmentStatus getStatus() {
        return status;
    }

    public void setStatus(InstallmentStatus status) {
        this.status = status;
    }
}
