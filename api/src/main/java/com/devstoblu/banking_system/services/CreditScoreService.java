package com.devstoblu.banking_system.services;

import com.devstoblu.banking_system.enums.loans.LoanStatus;
import com.devstoblu.banking_system.models.Usuario;
import com.devstoblu.banking_system.models.banking_account.Account;
import com.devstoblu.banking_system.repositories.loan.LoanRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;

@Service
public class CreditScoreService {

    /**
     * Calcula o score de crédito de um usuário baseado em:
     * Tempo de conta (+10 pontos/ano)
     * Saldo médio das contas (+1 ponto a cada R$100)
     * Histórico de pagamentos (+50 se sem atrasos)
     * Renda comprovada (+30 se > 0)
     */

    private final LoanRepository loanRepository;

    public CreditScoreService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public int calculateScore(Usuario usuario) {
        int score = 0;


        int years = Period.between(usuario.getAccountCreationDate(), LocalDate.now()).getYears();
        score += years * 10;

        BigDecimal totalBalance = BigDecimal.ZERO;
        int accountCount = 0;
        for (Account account : usuario.getAccounts()) {
            totalBalance = totalBalance.add(BigDecimal.valueOf(account.getBalance()));
            accountCount++;
        }
        if (accountCount > 0) {
            BigDecimal averageBalance = totalBalance.divide(BigDecimal.valueOf(accountCount), 0, RoundingMode.DOWN);
            score += averageBalance.divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN).intValue();

        }

        if (usuarioHasNoLatePayments(usuario)) {
            score += 50;
        }

        if (usuario.getIncome() != null && usuario.getIncome().compareTo(BigDecimal.ZERO) > 0) {
            score += 30;
        }

        return score;
    }


    public String evaluateLoanEligibility(Usuario usuario) {
        int score = calculateScore(usuario);

        if (score > 300) return "APROVADO_AUTOMATICO";
        else if (score >= 200) return "APROVADO_MANUAL";
        else return "REPROVADO";
    }

    private boolean usuarioHasNoLatePayments(Usuario usuario) {

        if (usuario.getAccounts() != null) {
            for (Account account : usuario.getAccounts()) {
                if (account.getBalance() < 0) {
                    return false;
                }
            }

            boolean temEmprestimoInadimplente = loanRepository.existsByUsuarioAndStatus(usuario, LoanStatus.INADIMPLENTE);


            if (temEmprestimoInadimplente) {
                return false;
            }

            boolean temFaturaVencida = loanRepository.existsByUsuarioAndStatusAndDueDateBefore(
                    usuario,
                    LoanStatus.ATIVO,
                    LocalDate.now()
            );

            if (temFaturaVencida) {
                return false;
            }
        }
            return true;


    }
}

