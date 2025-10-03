import { Component } from '@angular/core';
import { CommonModule, NgFor, NgClass, CurrencyPipe } from '@angular/common';
import { CreditCardComponent } from '../credit-card/credit-card';
import { InvoiceSummaryComponent } from "../invoice-summary/invoice-summary";
import { CreditCardService } from '../../../services/credit-card-service';
import { CreditCard } from '../../../models/credit-card.model';

@Component({
  selector: 'app-credit-card-dashboard',
  standalone: true,
  templateUrl: './credit-card-dashboard.html',
  styleUrl: './credit-card-dashboard.css',
  imports: [
    CommonModule,
    NgFor,
    NgClass,
    CurrencyPipe,
    CreditCardComponent,
    InvoiceSummaryComponent
]
})
export class CreditCardDashboardComponent {

  constructor(private creditCardService: CreditCardService) {}

  cards: CreditCard[] = [];
  totalLimit: number = 0;

  ngOnInit(): void {
    this.creditCardService.getAllCards().subscribe(cards => {
      this.cards = cards;
      this.totalLimit = this.cards.reduce((sum, card) => sum + card.creditLimit, 0);
    });
  }

  transactions = [
    { date: '2024-09-01', description: 'Amazon Purchase', amount: -150.75 },
    { date: '2024-09-03', description: 'TV', amount: -2500.00 },
  ];


  get invoiceTotal(): number {
    return this.transactions.filter(tx => tx.amount < 0).reduce((sum, tx) => sum + Math.abs(tx.amount), 0);
  }
}