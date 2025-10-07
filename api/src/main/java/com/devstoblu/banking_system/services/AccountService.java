package com.devstoblu.banking_system.services;

import com.devstoblu.banking_system.enums.TransferType;
import com.devstoblu.banking_system.models.Transaction;
import com.devstoblu.banking_system.models.Usuario;
import com.devstoblu.banking_system.models.banking_account.Account;
import com.devstoblu.banking_system.models.banking_account.CheckingAccount;
import com.devstoblu.banking_system.models.banking_account.SavingsAccount;

import com.devstoblu.banking_system.repositories.TransactionRepository;
import com.devstoblu.banking_system.repositories.UsuarioRepository;
import com.devstoblu.banking_system.repositories.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class AccountService {
  private final AccountRepository accountRepository;
  private final UsuarioRepository usuarioRepository;
  private final TransactionRepository transactionRepository;

  public AccountService(AccountRepository repository, UsuarioRepository usuarioRepository, TransactionRepository transactionRepository) {
    this.accountRepository = repository;
    this.usuarioRepository = usuarioRepository;
    this.transactionRepository = transactionRepository;
  }

  public List<Account> findAll() {
    return accountRepository.findAll();
  }

  public List<CheckingAccount> findAllCheckingAccounts() {
    return accountRepository.findAllCheckingAccounts();
  }

  public List<SavingsAccount> findAllSavingsAccounts() {
    return accountRepository.findAllSavingsAccounts();
  }

  public Optional<Account> findByAccountNumber(String accountNumber) {
    return accountRepository.findByAccountNumber(accountNumber);
  }

  public CheckingAccount createCheckingAccount(Long userId, double balance) {
    Usuario user = usuarioRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

    boolean hasChecking = user.getAccounts().stream().anyMatch(a -> a instanceof CheckingAccount);
    if (hasChecking) throw new IllegalArgumentException("Usuário já possui uma conta corrente.");

    CheckingAccount account = new CheckingAccount();
    account.setBalance(balance);
    account.setUsuario(user);

    return accountRepository.save(account);
  }

  public SavingsAccount createSavingsAccount(Long userId, double balance) {
    Usuario user = usuarioRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

    boolean hasSavings = user.getAccounts().stream().anyMatch(a -> a instanceof SavingsAccount);
    if (hasSavings) throw new IllegalArgumentException("Usuário já possui uma conta poupança.");

    SavingsAccount account = new SavingsAccount();
    account.setBalance(balance);
    account.setUsuario(user);

    return accountRepository.save(account);
  }

  public Account deposit(String accountNumber, Double value) {
    Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new RuntimeException("Conta não encontrada"));
    account.deposit(value);
    return accountRepository.save(account);
  }

  public Account withdraw(String accountNumber, Double value) {
    Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new RuntimeException("Conta não encontrada"));
    account.withdraw(value);
    return accountRepository.save(account);
  }

  // Deletar conta corrente e poupança
  public void delete(String accountNumber) {
    Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new RuntimeException("Conta não encontrada"));
    if (account.getBalance() != 0) {
      throw new RuntimeException("Não é possível deletar conta com saldo positivo ou negativo. Saldo atual: " + account.getBalance());
    }
    accountRepository.delete(account);
  }

  // Relatório e aplicação de taxas e rendimentos nas contas (metodo applyFeesAndMaintenance)
  public FeeApplicationResult applyFeesAndMaintenanceWithDetails() {
    List<Account> accounts = accountRepository.findAll();
    List<AccountUpdateDetail> detalhes = new ArrayList<>();

    for (Account c : accounts) {
      Double previousBalance = c.getBalance();
      c.applyFeesAndMaintenance();
      accountRepository.save(c);

      detalhes.add(new AccountUpdateDetail(
              c.getId(),
              c.getAccountNumber(),
              previousBalance,
              c.getBalance(),
              c.getAccountType()
      ));
    }
    return new FeeApplicationResult(detalhes);
  }

  // Records para a resposta
  public record FeeApplicationResult(List<AccountUpdateDetail> updades) {}

  public record AccountUpdateDetail(
          Long accountId,
          String accountNumber,
          Double previousBalance,
          Double currentBalance,
          String accountType

  ) {}

  @Transactional
  public Map<String, Object> transfer(String fromAccountNumber, String toAccountNumber, Double value, TransferType type) {
    if (value == null || value <= 0)
      throw new RuntimeException("O valor da transferência deve ser positivo.");

    Account from = accountRepository.findByAccountNumber(fromAccountNumber)
            .orElseThrow(() -> new RuntimeException("Conta de origem não encontrada."));
    Account to = accountRepository.findByAccountNumber(toAccountNumber)
            .orElseThrow(() -> new RuntimeException("Conta de destino não encontrada."));

    if (from.getAccountNumber().equals(to.getAccountNumber())) {
      throw new RuntimeException("Não é possível transferir para a mesma conta.");
    }

    // Executa a transferência
    switch (type) {
      case INTERNAL -> processInternalTransfer(from, to, value);
      case TED -> processTED(from, to, value);
      case PIX -> processPIX(from, to, value);
      default -> throw new RuntimeException("Tipo de transferência inválido.");
    }

    // Salva contas após alterações
    accountRepository.save(from);
    accountRepository.save(to);

    // Registra transação no banco
    registerTransactionHistory(from, to, value, type);

    // Retorna informações detalhadas
    Map<String, Object> response = new HashMap<>();
    response.put("fromAccount", from.getAccountNumber());
    response.put("toAccount", to.getAccountNumber());
    response.put("amount", value);
    response.put("type", type.name());
    response.put("fromBalanceAfter", from.getBalance());
    response.put("toBalanceAfter", to.getBalance());
    response.put("message", "Transferência realizada com sucesso!");
    return response;
  }

  // Transferência interna
  private void processInternalTransfer(Account from, Account to, Double value) {
    if (from instanceof SavingsAccount savings) {
      // Limite mensal de transferências da poupança
      LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
      LocalDateTime endOfMonth = LocalDateTime.now().withDayOfMonth(LocalDateTime.now().toLocalDate().lengthOfMonth())
              .withHour(23).withMinute(59).withSecond(59);

      long transfersThisMonth = transactionRepository
              .countByFromAccountAndTypeAndTimestampBetween(from, TransferType.INTERNAL, startOfMonth, endOfMonth);

      if (transfersThisMonth >= 3) {
        double fee = 3.0;
        double total = value + fee;
        if (from.getBalance() < total)
          throw new RuntimeException("Saldo insuficiente para transferência com taxa da poupança.");
        from.withdraw(total);
        to.deposit(value);
      } else {
        if (from.getBalance() < value)
          throw new RuntimeException("Saldo insuficiente.");
        from.withdraw(value);
        to.deposit(value);
      }
    } else {
      // Conta corrente: ilimitada
      if (from.getBalance() < value)
        throw new RuntimeException("Saldo insuficiente.");
      from.withdraw(value);
      to.deposit(value);
    }
  }

  // TED
  private void processTED(Account from, Account to, Double value) {
    LocalTime now = LocalTime.now();
    if (now.isBefore(LocalTime.of(6, 0)) || now.isAfter(LocalTime.of(17, 0))) {
      throw new RuntimeException("TED só pode ser realizada entre 06:00 e 17:00.");
    }

    double fee = (from instanceof SavingsAccount) ? 15.0 : 10.0;
    double total = value + fee;

    if (from.getBalance() < total)
      throw new RuntimeException("Saldo insuficiente para TED.");

    from.withdraw(total);
    to.deposit(value);
  }

  // PIX
  private void processPIX(Account from, Account to, Double value) {
    // Limite máximo por transação
    if (value > 5000.0)
      throw new RuntimeException("O limite máximo por transação do PIX é R$ 5000.");

    // Limite noturno
    LocalTime now = LocalTime.now();
    if (now.isAfter(LocalTime.of(20, 0)) || now.isBefore(LocalTime.of(6, 0))) {
      if (value > 1000.0)
        throw new RuntimeException("O limite noturno do PIX (20h-06h) é R$ 1000.");
    }

    // Limite diário
    LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
    LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

    long pixToday = transactionRepository
            .countByFromAccountAndTypeAndTimestampBetween(from, TransferType.PIX, startOfDay, endOfDay);

    if (pixToday >= 10) // por exemplo, máximo 10 PIX/dia
      throw new RuntimeException("Limite diário de PIX atingido.");

    if (from.getBalance() < value)
      throw new RuntimeException("Saldo insuficiente para PIX.");

    from.withdraw(value);
    to.deposit(value);
  }

  // Registro de histórico no banco
  private void registerTransactionHistory(Account from, Account to, Double value, TransferType type) {
    Transaction tx = new Transaction();
    tx.setFromAccount(from);
    tx.setToAccount(to);
    tx.setAmount(value);
    tx.setType(type);
    tx.setTimestamp(LocalDateTime.now());
    transactionRepository.save(tx);
  }

}
