import { Component, OnInit, Input, Output, EventEmitter, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AccountService, BankAccount } from '../../services/account.service';
import { AuthService } from '../../services/auth.service';

export interface CreateAccountModal {
  show: boolean;
  type: 'CHECKING' | 'SAVINGS' | null;
  balance: number;
}

export interface TransactionModal {
  show: boolean;
  type: 'DEPOSIT' | 'WITHDRAW' | null;
  accountNumber: string;
  amount: number;
}

@Component({
  selector: 'app-bank-accounts',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './bank-accounts.html'
})
export class BankAccountsComponent implements OnInit {
  @Input() showActions: boolean = true;
  @Output() accountClick = new EventEmitter<BankAccount>();
  @Output() createAccount = new EventEmitter<string>();

  private readonly accountService = inject(AccountService);
  private readonly authService = inject(AuthService);

  accounts: BankAccount[] = [];
  loading: boolean = false;
  currentUserId: number | null = null;
  
  createAccountModal: CreateAccountModal = {
    show: false,
    type: null,
    balance: 0
  };

  transactionModal: TransactionModal = {
    show: false,
    type: null,
    accountNumber: '',
    amount: 0
  };

  ngOnInit() {
    this.loadCurrentUser();
    this.loadUserAccounts();
  }

  private loadCurrentUser() {
    this.authService.getUserInfo().subscribe({
      next: (user) => {
        this.currentUserId = user.id;
      },
      error: (error) => {
        console.error('Error loading user info:', error);
      }
    });
  }

  private loadUserAccounts() {
    this.loading = true;
    this.accountService.getUserAccounts().subscribe({
      next: (accounts) => {
        this.accounts = accounts;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading accounts:', error);
        this.loading = false;
      }
    });
  }

  onAccountClick(account: BankAccount) {
    this.accountClick.emit(account);
  }

  onCreateAccount(accountType: string) {
    this.createAccountModal = {
      show: true,
      type: accountType as 'CHECKING' | 'SAVINGS',
      balance: 0
    };
  }

  onDeposit(account: BankAccount) {
    this.transactionModal = {
      show: true,
      type: 'DEPOSIT',
      accountNumber: account.accountNumber,
      amount: 0
    };
  }

  onWithdraw(account: BankAccount) {
    this.transactionModal = {
      show: true,
      type: 'WITHDRAW',
      accountNumber: account.accountNumber,
      amount: 0
    };
  }

  confirmCreateAccount() {
    if (!this.currentUserId || !this.createAccountModal.type) return;

    const createMethod = this.createAccountModal.type === 'CHECKING' 
      ? this.accountService.createCheckingAccount 
      : this.accountService.createSavingsAccount;

    createMethod.call(this.accountService, this.currentUserId, this.createAccountModal.balance).subscribe({
      next: () => {
        this.closeCreateAccountModal();
        this.loadUserAccounts();
        this.createAccount.emit(this.createAccountModal.type!);
      },
      error: (error) => {
        console.error('Error creating account:', error);
        alert('Erro ao criar conta: ' + (error.error?.message || error.message));
      }
    });
  }

  confirmTransaction() {
    if (!this.transactionModal.type || !this.transactionModal.accountNumber) return;

    const transactionMethod = this.transactionModal.type === 'DEPOSIT'
      ? this.accountService.deposit
      : this.accountService.withdraw;

    transactionMethod.call(this.accountService, this.transactionModal.accountNumber, this.transactionModal.amount).subscribe({
      next: () => {
        this.closeTransactionModal();
        this.loadUserAccounts();
      },
      error: (error) => {
        console.error('Error processing transaction:', error);
        alert('Erro na transação: ' + (error.error?.error || error.message));
      }
    });
  }

  closeCreateAccountModal() {
    this.createAccountModal = {
      show: false,
      type: null,
      balance: 0
    };
  }

  closeTransactionModal() {
    this.transactionModal = {
      show: false,
      type: null,
      accountNumber: '',
      amount: 0
    };
  }

  formatBalance(balance: number): string {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(balance);
  }

  getAccountTypeLabel(accountType: string): string {
    return accountType === 'CHECKING' ? 'Conta Corrente' : 'Conta Poupança';
  }

  getAccountTypeIcon(accountType: string): string {
    return accountType === 'CHECKING' 
      ? 'M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z'
      : 'M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z';
  }

  hasAccountType(accountType: string): boolean {
    return this.accounts.some(account => account.accountType === accountType);
  }
}
