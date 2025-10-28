import { CommonModule, CurrencyPipe, NgFor, NgIf } from '@angular/common';
import { Component, Input } from '@angular/core';
type InvoiceItem = { date: string; description: string; amount: number };

@Component({
  selector: 'app-invoice-summary',
  standalone: true,
  imports: [CommonModule,  CurrencyPipe],
  templateUrl: './invoice-summary.html',
  styleUrls: ['./invoice-summary.css']
})
export class InvoiceSummaryComponent {
  @Input() loading: boolean = false;
  @Input() items: InvoiceItem[] = [];
  @Input() invoiceTotal: number = 0;
  @Input() creditLimit: number = 0;
  
  get availableLimit(): number {
    return this.creditLimit - this.invoiceTotal;
  }

  get progressPercentage(): number {
    return this.creditLimit ? (this.invoiceTotal / this.creditLimit) * 100 : 0;
  }
}
