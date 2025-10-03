import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-invoice-summary',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './invoice-summary.html',
  styleUrl: './invoice-summary.css'
})
export class InvoiceSummaryComponent {
  @Input() invoiceTotal: number = 0;
  @Input() creditLimit: number = 0;
  
  get availableLimit(): number {
    return this.creditLimit - this.invoiceTotal;
  }

  get progressPercentage(): number {
    return this.creditLimit ? (this.invoiceTotal / this.creditLimit) * 100 : 0;
  }
}
