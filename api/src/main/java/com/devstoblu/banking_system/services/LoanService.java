package com.devstoblu.banking_system.services;

import com.devstoblu.banking_system.dto.loan.*;
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

    public LoanService(LoanRepository loanRepository, LoanInstallmentRepository installmentRepository){
        this.loanRepository = loanRepository;
        this.installmentRepository = installmentRepository;
    }

    public LoanSimulationDTO simulateLoan(LoanRequestDTO req){
        BigDecimal principal = req.getTotalAmount();
        int months = req.getNumberOfInstallments();
        BigDecimal monthlyRate = req.getInterestRate();

        BigDecimal installment = calculatePayment(principal, monthlyRate, months);
        BigDecimal total = installment.multiply(BigDecimal.valueOf(months)).setScale(2, RoundingMode.HALF_UP);

        LoanSimulationDTO dto = new LoanSimulationDTO();
        dto.setTotalAmount(principal);
        dto.setNumberOfInstallments(months);
        dto.setMonthlyRate(monthlyRate);
        dto.setMonthlyInstallment(installment);
        dto.setTotalToPay(total);
        return dto;
    }

    private BigDecimal calculatePayment(BigDecimal P, BigDecimal r, int n){
        if (r.compareTo(BigDecimal.ZERO) == 0) {
            return P.divide(BigDecimal.valueOf(n), 2, RoundingMode.HALF_UP);
        }

        BigDecimal onePlusR = BigDecimal.ONE.add(r);
        BigDecimal pow = BigDecimal.ONE.add(r).pow(n);
        BigDecimal factor = P.multiply(r).multiply(pow).divide(pow.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);// pow with negative not direct

        return factor.setScale(2, RoundingMode.HALF_UP);
    }


    public LoanResponseDTO createLoanRequest(LoanRequestDTO req){
        Loan loan = new Loan();
        loan.setTotalAmount(req.getTotalAmount());
        loan.setNumberOfInstallments(req.getNumberOfInstallments());
        loan.setInterestRate(req.getInterestRate());
        loan.setStatus(LoanStatus.EM_ANALISE);
        loan.setType(req.getType());

        loan = loanRepository.save(loan);

        // gerar parcelas com base na simulação
        BigDecimal installmentValue = simulateLoan(req).getMonthlyInstallment();
        List<LoanInstallment> parcels = new ArrayList<>();
        LocalDate base = LocalDate.now().plusMonths(1); // 1 mês para a primeira parcela
        for (int i = 1; i <= req.getNumberOfInstallments(); i++){
            LoanInstallment inst = new LoanInstallment();
            inst.setLoan(loan);
            inst.setNumber(i);
            inst.setDueDate(base.plusMonths(i-1));
            inst.setAmount(installmentValue);
            inst.setStatus(com.devstoblu.banking_system.enums.loans.InstallmentStatus.PENDENTE);
            parcels.add(inst);
        }
        installmentRepository.saveAll(parcels);

        LoanResponseDTO resp = new LoanResponseDTO();
        resp.setId(loan.getId());
        resp.setStatus(loan.getStatus());
        resp.setInterestRate(installmentValue);
        resp.setNumberOfInstallments(loan.getNumberOfInstallments());
        return resp;
    }

    public List<LoanResponseDTO> listLoans(Long userId){
        List<Loan> loans = loanRepository.findByUsuarioId(userId);
        return new ArrayList<>();
    }

    public LoanResponseDTO getLoanResponse(Long id){
        Loan loan = loanRepository.findById(id).orElseThrow();
        return new LoanResponseDTO();
    }

    public LoanResponseDTO approveLoan(Long id){
        Loan loan = loanRepository.findById(id).orElseThrow();
        loan.setStatus(LoanStatus.APROVADO);
        loanRepository.save(loan);
        return getLoanResponse(id);
    }

    public LoanResponseDTO rejectLoan(Long id){
        Loan loan = loanRepository.findById(id).orElseThrow();
        loan.setStatus(LoanStatus.REPROVADO);
        loanRepository.save(loan);
        return getLoanResponse(id);
    }
}
