package com.devstoblu.banking_system.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
public record CreditCardRequestDTO(
        @NotBlank String name,
        @NotBlank String cpf,
        @NotNull LocalDate dateOfBirth,
        @Email @NotBlank String email,
        @NotBlank String phoneNumber,
        @Valid @NotNull AddressDTO address,
        @NotNull @Positive BigDecimal monthlyIncome,
        @NotBlank String sourceIncome,
        String company,
        Integer employmentTimeMonths,
        @NotBlank String invoiceType,
        Integer preferredDueDate,
        @NotNull Boolean acceptTerms,
        @NotNull Boolean authorizationCreditConsultation
) {}
