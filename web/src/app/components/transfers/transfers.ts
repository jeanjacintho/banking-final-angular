import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LucideAngularModule } from 'lucide-angular';
import { AccountService, BankAccount, TransferRequest, TransferResponse } from '../../services/account.service';

interface TransferType {
  value: string;
  label: string;
  fee: string;
  description: string;
}

@Component({
  selector: 'app-transfers',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  templateUrl: './transfers.html',
  styles: [`
    .transfers-page {
      min-height: 100vh;
      background: var(--bg-primary);
    }
  `]
})
export class TransfersComponent implements OnInit {
  private readonly accountService = inject(AccountService);

  accounts: BankAccount[] = [];
  selectedFromAccount: BankAccount | null = null;
  selectedToAccount: BankAccount | null = null;
  toAccountNumber: string = '';
  transferAmount: number = 0;
  transferType: string = 'INTERNAL';
  isLoading: boolean = false;
  errorMessage: string = '';
  showSuccessModal: boolean = false;
  successMessage: string = '';

  transferTypes: TransferType[] = [
    {
      value: 'INTERNAL',
      label: 'Transferência Interna',
      fee: 'Sem taxa',
      description: 'Transferência entre contas do mesmo banco'
    },
    {
      value: 'PIX',
      label: 'PIX',
      fee: 'R$ 0,00',
      description: 'Transferência instantânea via PIX'
    },
    {
      value: 'TED',
      label: 'TED',
      fee: 'R$ 8,50',
      description: 'Transferência Eletrônica Disponível'
    }
  ];

  ngOnInit(): void {
    this.loadAccounts();
  }

  loadAccounts(): void {
    this.accountService.getUserAccounts().subscribe({
      next: (accounts) => {
        this.accounts = accounts;
      },
      error: (error) => {
        console.error('Erro ao carregar contas:', error);
        this.errorMessage = 'Erro ao carregar contas';
      }
    });
  }

  selectFromAccount(account: BankAccount): void {
    this.selectedFromAccount = account;
    this.errorMessage = '';
  }

  onToAccountNumberChange(): void {
    if (this.toAccountNumber && this.toAccountNumber.length >= 4) {
      this.accountService.getAccountByNumber(this.toAccountNumber).subscribe({
        next: (account) => {
          this.selectedToAccount = account;
          this.errorMessage = '';
        },
        error: (error) => {
          this.selectedToAccount = null;
          if (error.status === 404) {
            this.errorMessage = 'Conta não encontrada';
          } else {
            this.errorMessage = 'Erro ao buscar conta';
          }
        }
      });
    } else {
      this.selectedToAccount = null;
      this.errorMessage = '';
    }
  }

  calculateTotalWithFee(): number {
    if (this.transferAmount <= 0) return 0;
    
    let fee = 0;
    switch (this.transferType) {
      case 'INTERNAL':
        fee = 0;
        break;
      case 'PIX':
        fee = 0;
        break;
      case 'TED':
        fee = 8.50;
        break;
    }
    
    return this.transferAmount + fee;
  }

  executeTransfer(): void {
    if (!this.selectedFromAccount || !this.toAccountNumber || this.transferAmount <= 0) {
      this.errorMessage = 'Preencha todos os campos corretamente';
      return;
    }

    if (this.selectedFromAccount.balance < this.calculateTotalWithFee()) {
      this.errorMessage = 'Saldo insuficiente para realizar a transferência';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const transferRequest: TransferRequest = {
      fromAccount: this.selectedFromAccount.accountNumber,
      toAccount: this.toAccountNumber,
      amount: this.transferAmount,
      type: this.transferType
    };

    this.accountService.transfer(transferRequest).subscribe({
      next: (response: TransferResponse) => {
        this.isLoading = false;
        this.showSuccessModal = true;
        this.successMessage = response.message;
        this.resetForm();
        this.loadAccounts();
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Erro na transferência:', error);
        this.errorMessage = error.error?.message || 'Erro ao realizar transferência';
      }
    });
  }

  resetForm(): void {
    this.selectedFromAccount = null;
    this.selectedToAccount = null;
    this.toAccountNumber = '';
    this.transferAmount = 0;
    this.transferType = 'INTERNAL';
    this.errorMessage = '';
  }

  closeSuccessModal(): void {
    this.showSuccessModal = false;
    this.successMessage = '';
  }

  getAccountTypeIcon(accountType: string): string {
    return accountType === 'CHECKING' ? '🏦' : '💰';
  }

  getAccountTypeLabel(accountType: string): string {
    return accountType === 'CHECKING' ? 'Conta Corrente' : 'Conta Poupança';
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  }
}
