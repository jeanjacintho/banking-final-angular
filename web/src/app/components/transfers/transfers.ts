import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AccountService, BankAccount, TransferRequest, TransferResponse } from '../../services/account.service';

@Component({
  selector: 'app-transfers',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './transfers.html'
})
export class TransfersComponent implements OnInit {
  private accountService = inject(AccountService);

  accounts: BankAccount[] = [];
  selectedFromAccount: BankAccount | null = null;
  selectedToAccount: BankAccount | null = null;
  transferAmount: number = 0;
  transferType: string = 'PIX';
  toAccountNumber: string = '';
  
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  showSuccessModal = false;

  transferTypes = [
    { value: 'PIX', label: 'PIX', description: 'Transferência instantânea 24h', fee: 'Gratuita' },
    { value: 'INTERNAL', label: 'Transferência Interna', description: 'Entre contas do banco', fee: 'Gratuita*' },
    { value: 'TED', label: 'TED', description: 'Para outros bancos', fee: 'R$ 10-15' }
  ];

  ngOnInit() {
    this.loadAccounts();
  }

  loadAccounts() {
    this.accountService.getUserAccounts().subscribe({
      next: (accounts) => {
        this.accounts = accounts;
        if (accounts.length > 0) {
          this.selectedFromAccount = accounts[0];
        }
      },
      error: (error) => {
        console.error('Erro ao carregar contas:', error);
      }
    });
  }

  selectFromAccount(account: BankAccount) {
    this.selectedFromAccount = account;
    this.clearMessages();
  }

  selectToAccount(account: BankAccount) {
    this.selectedToAccount = account;
    this.toAccountNumber = account.accountNumber;
    this.clearMessages();
  }

  onToAccountNumberChange() {
    this.selectedToAccount = null;
    this.clearMessages();
  }

  findAccountByNumber(accountNumber: string): BankAccount | null {
    return this.accounts.find(acc => acc.accountNumber === accountNumber) || null;
  }

  validateTransfer(): string | null {
    if (!this.selectedFromAccount) {
      return 'Selecione a conta de origem';
    }

    if (!this.toAccountNumber.trim()) {
      return 'Informe o número da conta de destino';
    }

    if (this.selectedFromAccount.accountNumber === this.toAccountNumber) {
      return 'Não é possível transferir para a mesma conta';
    }

    if (this.transferAmount <= 0) {
      return 'Valor deve ser maior que zero';
    }

    if (this.transferAmount > this.selectedFromAccount.balance) {
      return 'Saldo insuficiente';
    }

    if (this.transferType === 'PIX' && this.transferAmount > 5000) {
      return 'Limite máximo do PIX é R$ 5.000';
    }

    const now = new Date();
    const currentHour = now.getHours();

    if (this.transferType === 'TED' && (currentHour < 6 || currentHour > 17)) {
      return 'TED só pode ser realizada entre 06:00 e 17:00';
    }

    if (this.transferType === 'PIX' && (currentHour > 20 || currentHour < 6) && this.transferAmount > 1000) {
      return 'Limite noturno do PIX é R$ 1.000';
    }

    return null;
  }

  calculateTotalWithFee(): number {
    if (!this.selectedFromAccount) return this.transferAmount;

    let fee = 0;
    if (this.transferType === 'TED') {
      fee = this.selectedFromAccount.accountType === 'SAVINGS' ? 15 : 10;
    } else if (this.transferType === 'INTERNAL' && this.selectedFromAccount.accountType === 'SAVINGS') {
      fee = 3;
    }

    return this.transferAmount + fee;
  }

  executeTransfer() {
    const validationError = this.validateTransfer();
    if (validationError) {
      this.errorMessage = validationError;
      return;
    }

    this.isLoading = true;
    this.clearMessages();

    const transferRequest: TransferRequest = {
      fromAccount: this.selectedFromAccount!.accountNumber,
      toAccount: this.toAccountNumber,
      amount: this.transferAmount,
      type: this.transferType
    };

    this.accountService.transfer(transferRequest).subscribe({
      next: (response: TransferResponse) => {
        this.successMessage = response.message;
        this.showSuccessModal = true;
        this.loadAccounts();
        this.resetForm();
      },
      error: (error) => {
        this.errorMessage = error.error?.error || 'Erro ao realizar transferência';
      },
      complete: () => {
        this.isLoading = false;
      }
    });
  }

  resetForm() {
    this.transferAmount = 0;
    this.toAccountNumber = '';
    this.selectedToAccount = null;
    this.transferType = 'PIX';
  }

  clearMessages() {
    this.errorMessage = '';
    this.successMessage = '';
  }

  closeSuccessModal() {
    this.showSuccessModal = false;
    this.successMessage = '';
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  }

  getAccountTypeLabel(type: string): string {
    return type === 'CHECKING' ? 'Conta Corrente' : 'Poupança';
  }

  getAccountTypeIcon(type: string): string {
    return type === 'CHECKING' ? '🏦' : '💰';
  }
}
