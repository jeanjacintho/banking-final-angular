import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AccountService, TransactionHistoryItem } from '../../services/account.service';
import { CreditCardService } from '../../services/credit-card-service';
import { CreditCard } from '../../models/credit-card.model';
import { CreditCardTransaction } from '../../models/credit-card-transaction.model';
import { LucideAngularModule } from 'lucide-angular';
import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
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
  private readonly creditCardService = inject(CreditCardService);

  items: TransactionHistoryItem[] = [];
  isLoading: boolean = false;
  errorMessage: string = '';

  ngOnInit(): void {
    this.fetch();
  }

  fetch(): void {
    this.isLoading = true;
    this.errorMessage = '';

    // Busca transações bancárias e de cartão de crédito em paralelo
    forkJoin({
      bankTransactions: this.accountService.getMyTransactions().pipe(
        catchError((error) => {
          console.log('Erro ao buscar transações bancárias:', error);
          return of([]);
        })
      ),
      creditCards: this.creditCardService.getAllCards().pipe(
        catchError((error) => {
          console.log('Erro ao buscar cartões:', error);
          return of([]);
        })
      )
    }).subscribe({
      next: ({ bankTransactions, creditCards }) => {
        console.log('Cartões encontrados:', creditCards.length);
        console.log('Transações bancárias encontradas:', bankTransactions.length);

        // Busca transações de todos os cartões
        const cardTransactionRequests = creditCards.map(card =>
          this.creditCardService.listTransactions(card.id).pipe(
            catchError((error) => {
              console.log(`Erro ao buscar transações do cartão ${card.id}:`, error);
              return of([]);
            }),
            map(txs => {
              console.log(`Transações encontradas para cartão ${card.id}:`, txs.length);
              return txs.map(tx => this.mapCreditCardTransactionToHistoryItem(tx, card));
            })
          )
        );

        if (cardTransactionRequests.length === 0) {
          // Se não há cartões, apenas mostra transações bancárias
          console.log('Nenhum cartão encontrado, mostrando apenas transações bancárias');
          this.items = this.sortAndLimit(bankTransactions);
          this.isLoading = false;
          return;
        }

        forkJoin(cardTransactionRequests).subscribe({
          next: (cardTransactionsArrays) => {
            // Flatten todas as transações de cartão
            const allCardTransactions = cardTransactionsArrays.flat();
            console.log('Total de transações de cartão:', allCardTransactions.length);
            
            // Mescla transações bancárias e de cartão
            const allTransactions = [...bankTransactions, ...allCardTransactions];
            console.log('Total de transações (bancárias + cartão):', allTransactions.length);
            
            // Ordena por data (mais recente primeiro) e limita aos 5 últimos
            this.items = this.sortAndLimit(allTransactions);
            console.log('Transações finais após ordenação e limite:', this.items.length);
            console.log('Items:', this.items);
            this.isLoading = false;
          },
          error: (error) => {
            console.error('Erro ao carregar transações de cartão:', error);
            // Em caso de erro, mostra apenas transações bancárias
            this.items = this.sortAndLimit(bankTransactions);
            this.isLoading = false;
          }
        });
      },
      error: (error) => {
        console.error('Erro ao carregar transações:', error);
        if (error?.status === 404) {
          this.items = [];
          this.errorMessage = '';
        } else {
          this.errorMessage = 'Erro ao carregar histórico de transações';
        }
        this.isLoading = false;
      }
    });
  }

  private mapCreditCardTransactionToHistoryItem(
    tx: CreditCardTransaction,
    card: CreditCard
  ): TransactionHistoryItem {
    // Garante que amount seja um número
    const amount = typeof tx.amount === 'number' 
      ? tx.amount 
      : (typeof tx.amount === 'string' ? parseFloat(tx.amount) : 0);
    
    console.log('Mapeando transação:', {
      id: tx.id,
      amount: tx.amount,
      amountConverted: amount,
      merchantName: tx.merchantName,
      createdAt: tx.createdAt
    });

    return {
      id: tx.id,
      fromAccount: null,
      toAccount: null,
      amount: amount,
      type: 'CREDIT_CARD',
      timestamp: tx.createdAt,
      merchantName: tx.merchantName || 'Compra no cartão',
      installmentsTotal: tx.installmentsTotal
    };
  }

  private sortAndLimit(transactions: TransactionHistoryItem[]): TransactionHistoryItem[] {
    return transactions
      .sort((a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime())
      .slice(0, 5);
  }

  getTypeLabel(type: string): string {
    const labels: { [key: string]: string } = {
      'INTERNAL': 'Interna',
      'TED': 'TED',
      'PIX': 'PIX',
      'DEPOSIT': 'Depósito',
      'WITHDRAW': 'Saque',
      'CREDIT_CARD': 'Cartão de Crédito'
    };
    return labels[type] || type;
  }

  getArrow(type: string): string {
    if (type === 'DEPOSIT') return '→';
    if (type === 'WITHDRAW') return '←';
    if (type === 'CREDIT_CARD') return '←';
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
           transaction.type === 'CREDIT_CARD' ||
           (transaction.type !== 'INTERNAL' && 
            transaction.type !== 'DEPOSIT' && 
            transaction.fromAccount !== null);
  }

  getTransactionName(transaction: TransactionHistoryItem): string {
    if (transaction.type === 'CREDIT_CARD') {
      const name = transaction.merchantName || 'Compra no cartão';
      const installments = transaction.installmentsTotal 
        ? ` (${transaction.installmentsTotal}x)` 
        : '';
      return `${name}${installments}`;
    } else if (transaction.type === 'DEPOSIT') {
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
    if (transaction.type === 'CREDIT_CARD') {
      return 'credit-card';
    } else if (transaction.type === 'INTERNAL' || transaction.type === 'PIX' || transaction.type === 'TED') {
      return 'transfer';
    } else if (transaction.type === 'DEPOSIT') {
      return 'deposit';
    } else if (transaction.type === 'WITHDRAW') {
      return 'wallet';
    }
    return 'wallet';
  }

  getTransactionCategory(transaction: TransactionHistoryItem): string {
    if (transaction.type === 'CREDIT_CARD') {
      return 'Compra no cartão';
    } else if (transaction.type === 'DEPOSIT') {
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
