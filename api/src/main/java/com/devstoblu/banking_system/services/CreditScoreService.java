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

    private final LoanRepository loanRepository;

    public CreditScoreService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    /**
     * Calcula o score de crédito do usuário baseado em:
     * - Tempo de conta (+10 pontos por ano)
     * - Saldo total das contas (+1 ponto a cada R$100)
     * - Histórico de pagamentos (+50 se sem atrasos)
     * - Renda comprovada (+30 se renda > 0)
     */
    public int calculateScore(Usuario usuario) {
        int score = 0;

        // Tempo de conta
        int years = Period.between(usuario.getAccountCreationDate(), LocalDate.now()).getYears();
        score += years * 10;

        // Saldo total das contas
        BigDecimal totalBalance = usuario.getAccounts().stream()
                .map(account -> BigDecimal.valueOf(account.getBalance()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        score += totalBalance.divide(BigDecimal.valueOf(100), RoundingMode.DOWN).intValue();

        // Histórico de pagamentos
        if (hasNoLatePayments(usuario)) {
            score += 50;
        }

        // Renda comprovada
        if (usuario.getIncome() != null && usuario.getIncome().compareTo(BigDecimal.ZERO) > 0) {
            score += 30;
        }

        return score;
    }

    /**
     * Avalia a elegibilidade de empréstimo baseado no score:
     * - >300: aprovação automática
     * - 200-300: aprovação manual (agora tratado como aprovação automática no LoanService)
     * - <200: reprovado
     */
    public LoanEligibilityResult evaluateLoanEligibility(Usuario usuario) {
        int score = calculateScore(usuario);

        if (score > 300) return LoanEligibilityResult.APROVADO_AUTOMATICO;
        else if (score >= 200) return LoanEligibilityResult.APROVADO_MANUAL;
        else return LoanEligibilityResult.REPROVADO;
    }

    /**
     * Verifica se o usuário não possui pagamentos atrasados.
     * Considera:
     * - Empréstimos inadimplentes
     * - Empréstimos ativos com parcelas vencidas
     */

    private boolean hasNoLatePayments(Usuario usuario) {
        // Verifica se existe algum empréstimo inadimplente
        boolean hasInadimplente = loanRepository.existsByUsuarioAndStatus(usuario, LoanStatus.INADIMPLENTE);

        // Verifica se existe empréstimo ativo com parcela vencida
        boolean hasOverdue = loanRepository.existsByUsuarioAndStatusAndDueDateBefore(
                usuario,
                LoanStatus.ATIVO,
                LocalDate.now()
        );

        return !hasInadimplente && !hasOverdue;
    }

    // Enum para retorno da elegibilidade
    public enum LoanEligibilityResult {
        APROVADO_AUTOMATICO,
        APROVADO_MANUAL,
        REPROVADO
    }
}