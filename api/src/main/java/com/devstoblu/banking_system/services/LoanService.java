package com.devstoblu.banking_system.services;

import com.devstoblu.banking_system.dto.loan.*;
import com.devstoblu.banking_system.enums.loans.InstallmentStatus;
import com.devstoblu.banking_system.enums.loans.LoanStatus;
import com.devstoblu.banking_system.models.loan.Loan;
import com.devstoblu.banking_system.models.loan.LoanInstallment;
import com.devstoblu.banking_system.repositories.loan.LoanInstallmentRepository;
import com.devstoblu.banking_system.repositories.loan.LoanRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository installmentRepository;

    public LoanService(LoanRepository loanRepository, LoanInstallmentRepository installmentRepository) {
        this.loanRepository = loanRepository;
        this.installmentRepository = installmentRepository;
    }

    /**
     * Simula um empréstimo no sistema PRICE.
     * Recebe valor, taxa e número de parcelas e retorna DTO com totais.
     */
    public LoanSimulationDTO simulateLoan(LoanRequestDTO req) {
        BigDecimal principal = req.getTotalAmount();
        int months = req.getNumberOfInstallments();
        BigDecimal monthlyRate = req.getInterestRate();

        // Se taxa foi informada como percentual, converte para decimal
        if (monthlyRate.compareTo(BigDecimal.ONE) > 0) {
            monthlyRate = monthlyRate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        }

        BigDecimal installment = calculatePayment(principal, monthlyRate, months);
        BigDecimal totalToPay = installment.multiply(BigDecimal.valueOf(months)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalInterest = totalToPay.subtract(principal).setScale(2, RoundingMode.HALF_UP);

        LoanSimulationDTO dto = new LoanSimulationDTO();
        dto.setRequestedAmount(principal.setScale(2, RoundingMode.HALF_UP));
        dto.setNumberOfInstallments(months);
        dto.setMonthlyRate(monthlyRate.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP)); // exibir como %
        dto.setMonthlyInstallment(installment);
        dto.setTotalAmount(totalToPay);
        dto.setTotalInterest(totalInterest);
        dto.setLoanType(req.getType());
        return dto;
    }

    /**
     * Fórmula PRICE (parcelas fixas)
     * A = P * [ i * (1+i)^n ] / [ (1+i)^n - 1 ]
     */
    private BigDecimal calculatePayment(BigDecimal P, BigDecimal r, int n) {
        if (r.compareTo(BigDecimal.ZERO) == 0) {
            return P.divide(BigDecimal.valueOf(n), 2, RoundingMode.HALF_UP);
        }

        double rate = r.doubleValue();
        double factor = (rate * Math.pow(1 + rate, n)) / (Math.pow(1 + rate, n) - 1);
        BigDecimal installment = BigDecimal.valueOf(factor).multiply(P).setScale(2, RoundingMode.HALF_UP);
        return installment;
    }

    /**
     * Cria uma solicitação de empréstimo e gera as parcelas.
     */
    public LoanResponseDTO createLoanRequest(LoanRequestDTO req) {
        Loan loan = new Loan();
        loan.setTotalAmount(req.getTotalAmount());
        loan.setNumberOfInstallments(req.getNumberOfInstallments());
        loan.setInterestRate(req.getInterestRate());
        loan.setStatus(LoanStatus.EM_ANALISE);
        loan.setType(req.getType());

        loan = loanRepository.save(loan);

        // gerar parcelas com base na simulação
        LoanSimulationDTO simulation = simulateLoan(req);
        BigDecimal installmentValue = simulation.getMonthlyInstallment();

        List<LoanInstallment> installments = new ArrayList<>();
        LocalDate firstDueDate = LocalDate.now().plusMonths(1);

        for (int i = 1; i <= req.getNumberOfInstallments(); i++) {
            LoanInstallment inst = new LoanInstallment();
            inst.setLoan(loan);
            inst.setNumber(i);
            inst.setDueDate(firstDueDate.plusMonths(i - 1));
            inst.setAmount(installmentValue);
            inst.setStatus(InstallmentStatus.PENDENTE);
            installments.add(inst);
        }

        installmentRepository.saveAll(installments);

        LoanResponseDTO resp = new LoanResponseDTO();
        resp.setId(loan.getId());
        resp.setStatus(loan.getStatus());
        resp.setInterestRate(req.getInterestRate());
        resp.setNumberOfInstallments(loan.getNumberOfInstallments());
        resp.setTotalAmount(loan.getTotalAmount());
        resp.setMonthlyInstallment(installmentValue);
        return resp;
    }

    public List<LoanResponseDTO> listLoans(Long userId) {
        List<Loan> loans = loanRepository.findByUsuarioId(userId);
        List<LoanResponseDTO> responses = new ArrayList<>();

        for (Loan loan : loans) {
            LoanResponseDTO dto = new LoanResponseDTO();
            dto.setId(loan.getId());
            dto.setStatus(loan.getStatus());
            dto.setInterestRate(loan.getInterestRate());
            dto.setNumberOfInstallments(loan.getNumberOfInstallments());
            dto.setTotalAmount(loan.getTotalAmount());
            responses.add(dto);
        }
        return responses;
    }

    public LoanResponseDTO getLoanResponse(Long id) {
        Loan loan = loanRepository.findById(id).orElseThrow();
        LoanResponseDTO dto = new LoanResponseDTO();
        dto.setId(loan.getId());
        dto.setStatus(loan.getStatus());
        dto.setInterestRate(loan.getInterestRate());
        dto.setNumberOfInstallments(loan.getNumberOfInstallments());
        dto.setTotalAmount(loan.getTotalAmount());
        return dto;
    }

    public LoanResponseDTO approveLoan(Long id) {
        Loan loan = loanRepository.findById(id).orElseThrow();
        loan.setStatus(LoanStatus.APROVADO);
        loanRepository.save(loan);
        return getLoanResponse(id);
    }

    public LoanResponseDTO rejectLoan(Long id) {
        Loan loan = loanRepository.findById(id).orElseThrow();
        loan.setStatus(LoanStatus.REPROVADO);
        loanRepository.save(loan);
        return getLoanResponse(id);
    }
}
