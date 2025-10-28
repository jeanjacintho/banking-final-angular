import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideAngularModule } from 'lucide-angular';
import { AccountService, BankAccount } from '../../services/account.service';

@Component({
  selector: 'app-account-balance',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './account-balance.html',
  styleUrls: ['./account-balance.css']
})
export class AccountBalanceComponent implements OnInit {
  private readonly accountService = inject(AccountService);

  accounts: BankAccount[] = [];
  isLoading = false;
  errorMessage = '';
  hideValues = false;

  ngOnInit(): void {
    this.loadAccounts();
  }

  loadAccounts(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.accountService.getUserAccounts().subscribe({
      next: (accounts) => {
        this.accounts = accounts;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Erro ao carregar contas:', error);
        if (error?.status === 404) {
          this.accounts = [];
          this.errorMessage = '';
        } else {
          this.errorMessage = 'Erro ao carregar contas';
        }
        this.isLoading = false;
      }
    });
  }

  getCheckingAccount(): BankAccount | undefined {
    return this.accounts.find(account => account.accountType === 'CHECKING');
  }

  getSavingsAccount(): BankAccount | undefined {
    return this.accounts.find(account => account.accountType === 'SAVINGS');
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  }

  hasActiveAccounts(): boolean {
    return this.accounts.length > 0;
  }

  toggleHideValues(): void {
    this.hideValues = !this.hideValues;
  }

  getMaskedValue(value: number): string {
    if (this.hideValues) {
      return '••••••';
    }
    return this.formatCurrency(value);
  }
}
