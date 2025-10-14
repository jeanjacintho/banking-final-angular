import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AccountService, TransactionHistoryItem } from '../../services/account.service';
import { LucideAngularModule } from 'lucide-angular';
@Component({
  selector: 'app-transfer-history',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './transfer-history.html',
  styles: [`
    .transfer-history {
      background: var(--bg-card);
      border: 1px solid var(--border-color);
      border-radius: 0.75rem;
      padding: 1rem;
      display: flex;
      flex-direction: column;
      gap: 0.75rem;
    }
  `]
})
export class TransferHistoryComponent implements OnInit {
  private readonly accountService = inject(AccountService);

  items: TransactionHistoryItem[] = [];
  isLoading: boolean = false;
  errorMessage: string = '';

  ngOnInit(): void {
    this.fetch();
  }

  fetch(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.accountService.getMyTransactions().subscribe({
      next: (transactions) => {
        // Limita para os últimos 5 registros
        this.items = transactions.slice(0, 5);
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Erro ao carregar transações:', error);
        this.errorMessage = 'Erro ao carregar histórico de transações';
        this.isLoading = false;
      }
    });
  }

  getTypeLabel(type: string): string {
    const labels: { [key: string]: string } = {
      'INTERNAL': 'Interna',
      'TED': 'TED',
      'PIX': 'PIX',
      'DEPOSIT': 'Depósito',
      'WITHDRAW': 'Saque'
    };
    return labels[type] || type;
  }

  getArrow(type: string): string {
    if (type === 'DEPOSIT') return '→';
    if (type === 'WITHDRAW') return '←';
    return '↔';
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  }

  formatDate(timestamp: string): string {
    const date = new Date(timestamp);
    return date.toLocaleDateString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  isOutgoing(transaction: TransactionHistoryItem): boolean {
    return transaction.type === 'WITHDRAW' || 
           (transaction.type !== 'INTERNAL' && 
            transaction.type !== 'DEPOSIT' && 
            transaction.fromAccount !== null);
  }

  getTransactionName(transaction: TransactionHistoryItem): string {
    if (transaction.type === 'DEPOSIT') {
      return 'Depósito';
    } else if (transaction.type === 'WITHDRAW') {
      return 'Saque';
    } else if (transaction.type === 'PIX') {
      return 'Transferência PIX';
    } else if (transaction.type === 'TED') {
      return 'Transferência TED';
    } else if (transaction.type === 'INTERNAL') {
      return 'Transferência Interna';
    }
    return 'Transação';
  }

  formatAmount(transaction: TransactionHistoryItem): string {
    const amount = transaction.amount;
    const isOutgoing = this.isOutgoing(transaction);
    const prefix = isOutgoing ? '-' : '+';
    
    return `${prefix} $ ${amount.toFixed(2).replace('.', ',')}`;
  }

  getTransactionIcon(transaction: TransactionHistoryItem): string {
    if (transaction.type === 'INTERNAL' || transaction.type === 'PIX' || transaction.type === 'TED') {
      return 'transfer';
    } else if (transaction.type === 'DEPOSIT') {
      return 'deposit';
    } else if (transaction.type === 'WITHDRAW') {
      return 'wallet';
    }
    return 'wallet';
  }

  getTransactionCategory(transaction: TransactionHistoryItem): string {
    if (transaction.type === 'DEPOSIT') {
      return 'Depósito';
    } else if (transaction.type === 'WITHDRAW') {
      return 'Saque';
    } else if (transaction.type === 'PIX') {
      return 'Transferência PIX';
    } else if (transaction.type === 'TED') {
      return 'Transferência TED';
    } else if (transaction.type === 'INTERNAL') {
      return 'Transferência Interna';
    }
    return 'Transação';
  }
}
