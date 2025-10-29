import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideAngularModule } from 'lucide-angular';
import { CreditCardService } from '../../../services/credit-card-service';
import { CreditCard } from '../../../models/credit-card.model';
import { CreditCardTransaction } from '../../../models/credit-card-transaction.model';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

type CardHistoryItem = {
  id: number;
  card: CreditCard;
  merchantName: string;
  amount: number;
  createdAt: string;
  installmentsTotal?: number;
};

@Component({
  selector: 'app-credit-card-history',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './credit-card-history.html'
})
export class CreditCardHistoryComponent implements OnInit {
  private readonly creditCardService = inject(CreditCardService);

  items: CardHistoryItem[] = [];
  isLoading = false;
  errorMessage = '';

  ngOnInit(): void {
    this.load();
  }

  private load(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.creditCardService.getAllCards().pipe(
      catchError((err) => {
        console.log('Erro ao buscar cartões:', err);
        return of([] as CreditCard[]);
      })
    ).subscribe((cards) => {
      if (!cards?.length) {
        this.items = [];
        this.isLoading = false;
        return;
      }

      const requests = cards.map((card) =>
        this.creditCardService.listTransactions(card.id).pipe(
          catchError((err) => {
            console.log('Erro ao buscar transações do cartão', card.id, err);
            return of([] as CreditCardTransaction[]);
          })
        )
      );

      forkJoin(requests).subscribe({
        next: (results) => {
          const merged: CardHistoryItem[] = [];
          results.forEach((txs, idx) => {
            const card = cards[idx];
            txs.forEach((t) => {
              const amount = typeof t.amount === 'number' ? t.amount : parseFloat(String(t.amount));
              merged.push({
                id: t.id,
                card,
                merchantName: t.merchantName || 'Compra no cartão',
                amount,
                createdAt: t.createdAt,
                installmentsTotal: t.installmentsTotal,
              });
            });
          });

          this.items = merged
            .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
            .slice(0, 5);
          this.isLoading = false;
        },
        error: () => {
          this.items = [];
          this.isLoading = false;
          this.errorMessage = 'Erro ao carregar gastos do cartão';
        }
      });
    });
  }

  formatAmount(value: number): string {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
  }

  formatDate(value: string): string {
    const d = new Date(value);
    return d.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' });
  }
}


