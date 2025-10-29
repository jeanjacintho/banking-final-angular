import { CommonModule, CurrencyPipe, NgFor, NgIf } from '@angular/common';
import { Component, Input } from '@angular/core';
type InvoiceItem = { date: string; description: string; amount: number };

@Component({
  selector: 'app-invoice-summary',
  standalone: true,
  imports: [CommonModule,  CurrencyPipe],
  templateUrl: './invoice-summary.html',
  styleUrl: './invoice-summary.css'
})
export class InvoiceSummaryComponent {
  @Input() loading: boolean = false;
  @Input() items: InvoiceItem[] = [];
  @Input() invoiceTotal: number = 0;
  @Input() creditLimit: number = 0;
  @Input() availableLimit?: number; // Pode ser passado diretamente do backend
  
  get calculatedAvailableLimit(): number {
    // Usa o availableLimit do backend se fornecido, senão calcula
    if (this.availableLimit !== undefined && this.availableLimit !== null) {
      return this.availableLimit;
    }
    return this.creditLimit - this.invoiceTotal;
  }

  get usedAmount(): number {
    return this.creditLimit - this.calculatedAvailableLimit;
  }

  get progressPercentage(): number {
    if (!this.creditLimit || this.creditLimit === 0) return 0;
    // Calcula porcentagem baseado no valor usado
    const used = this.usedAmount;
    return Math.min((used / this.creditLimit) * 100, 100);
  }
}
