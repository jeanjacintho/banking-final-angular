import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AccountService, TransactionHistoryItem } from '../../services/account.service';

@Component({
  selector: 'app-transfer-history',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './transfer-history.html'
})
export class TransferHistoryComponent implements OnInit {
  private accountService = inject(AccountService);

  isLoading = false;
  errorMessage = '';
  items: TransactionHistoryItem[] = [];

  ngOnInit() {
    this.fetch();
  }

  fetch() {
    this.isLoading = true;
    this.errorMessage = '';
    this.accountService.getMyTransactions().subscribe({
      next: (items) => {
        this.items = items;
      },
      error: () => {
        this.errorMessage = 'Não foi possível carregar o histórico de transferências';
      },
      complete: () => {
        this.isLoading = false;
      }
    });
  }

  formatCurrency(v: number) {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(v);
  }

  formatDate(iso: string) {
    const d = new Date(iso);
    return d.toLocaleString('pt-BR');
  }

  getTypeLabel(type: string): string {
    switch (type) {
      case 'PIX': return 'PIX';
      case 'TED': return 'TED';
      case 'INTERNAL': return 'Transferência';
      case 'DEPOSIT': return 'Depósito';
      case 'WITHDRAW': return 'Saque';
      default: return type;
    }
  }

  getArrow(type: string): string {
    switch (type) {
      case 'DEPOSIT': return '↓';
      case 'WITHDRAW': return '↑';
      default: return '→';
    }
  }
}


