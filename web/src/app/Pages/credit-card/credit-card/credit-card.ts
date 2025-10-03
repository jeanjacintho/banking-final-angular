import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-credit-card',
  standalone: true,
  templateUrl: './credit-card.html',
  styleUrl: './credit-card.css'
})
export class CreditCardComponent {
  @Input() creditCard!: {
    id: number;
    cardNumber: string;
    expirationDate: string;
    brand: string;
    cardHolderName: string;
  }; // Recebe os dados de fora

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