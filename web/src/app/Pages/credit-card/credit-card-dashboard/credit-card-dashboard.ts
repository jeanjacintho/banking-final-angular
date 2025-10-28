import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { CreditCardComponent } from '../../../components/credit-card/credit-card/credit-card';
import { InvoiceSummaryComponent } from '../../../components/credit-card/invoice-summary/invoice-summary';
import { CreditCardService } from '../../../services/credit-card-service';
import { CreditCard } from '../../../models/credit-card.model';
import { RouterModule } from '@angular/router';

type InvoiceItem = {
  date: string;
  description: string;
  amount: number;
};

@Component({
  selector: 'app-credit-card-dashboard',
  standalone: true,
  templateUrl: './credit-card-dashboard.html',
  imports: [
    CommonModule,
    CurrencyPipe,
    CreditCardComponent,
    InvoiceSummaryComponent,
    RouterModule,
  ],
})
export class CreditCardDashboardComponent implements OnInit {
  constructor(private creditCardService: CreditCardService) {}

  cards: CreditCard[] = [];
  totalLimit: number = 0;

  selectedCard: CreditCard | null = null;
  invoiceLoading = false;
  invoiceItems: InvoiceItem[] = [];

  ngOnInit(): void {
    this.creditCardService.getAllCards().subscribe((cards) => {
      this.cards = cards;
      this.totalLimit = this.cards.reduce((sum, card) => sum + card.creditLimit, 0);

      // Opcional: já seleciona o primeiro card
      if (this.cards.length && !this.selectedCard) {
        this.onSelectCard(this.cards[0]);
      }
    });
  }

  onSelectCard(card: CreditCard) {
    if (this.selectedCard?.id === card.id) return;
    this.selectedCard = card;
    this.loadInvoice(card.id);
  }

  private loadInvoice(cardId: number) {
    this.invoiceLoading = true;
    // Substitua por chamada real quando tiver o endpoint pronto
    // this.creditCardService.getInvoice(cardId).subscribe({
    //   next: items => { this.invoiceItems = items; this.invoiceLoading = false; },
    //   error: () => { this.invoiceItems = []; this.invoiceLoading = false; }
    // });

    // Mock temporário
    setTimeout(() => {
      this.invoiceItems = [
        { date: '2025-10-03', description: 'Mercado', amount: -320.45 },
        { date: '2025-10-05', description: 'Gasolina', amount: -210.0 },
        { date: '2025-10-10', description: 'Pagamento anterior', amount: 320.45 },
      ];
      this.invoiceLoading = false;
    }, 300);
  }

  get invoiceTotalBySelected(): number {
    return this.invoiceItems
      .filter((tx) => tx.amount < 0)
      .reduce((sum, tx) => sum + Math.abs(tx.amount), 0);
  }
}
