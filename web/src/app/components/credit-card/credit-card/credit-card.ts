import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CreditCard } from '../../../models/credit-card.model';

@Component({
  selector: 'app-credit-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './credit-card.html',
  styleUrls: ['./credit-card.css']
})
export class CreditCardComponent {
  @Input() creditCard!: {
    id: number;
    cardNumber: string;
    expirationDate: string;
    brand: string;
    cardHolderName: string;
  }; // Recebe os dados de fora

  @Input() card!: CreditCard;
  @Input() selected = false;
  @Output() select = new EventEmitter<CreditCard>();

  onClick() {
    this.select.emit(this.card);
  }

  // Mostra apenas os 4 últimos dígitos
  maskCardNumber(num: string): string {
    if (!num) return '';
    const last4 = num.slice(-4);
    return '•••• •••• •••• ' + last4;
  }

  // Garante que validade fique MM/YY
  formatExpiry(date: string): string {
    if (!date) return '';
    const parts = date.split('/');
    if (parts.length === 2 && parts[1].length === 4) {
      return `${parts[0]}/${parts[1].slice(-2)}`;
    }
    return date;
  }
}