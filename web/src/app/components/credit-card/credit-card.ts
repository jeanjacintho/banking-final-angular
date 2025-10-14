import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LucideAngularModule } from 'lucide-angular';

export interface CreditCard {
  id: number;
  cardNumber: string;
  cardHolder: string;
  expiryDate: string;
  balance: number;
  cardType: 'VISA' | 'MASTERCARD' | 'AMEX';
  isActive: boolean;
}

@Component({
  selector: 'app-credit-card',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  templateUrl: './credit-card.html'
})
export class CreditCardComponent implements OnInit {
  
  cards: CreditCard[] = [];
  currentCardIndex = 0;

  ngOnInit() {
    this.loadCards();
  }

  loadCards() {
    // Mock data - em produção viria de um serviço
    this.cards = [
      {
        id: 1,
        cardNumber: '4532 1234 5678 2431',
        cardHolder: 'João Silva',
        expiryDate: '17/24',
        balance: 1023.00,
        cardType: 'VISA',
        isActive: true
      },
      {
        id: 2,
        cardNumber: '5555 4444 3333 2222',
        cardHolder: 'João Silva',
        expiryDate: '12/25',
        balance: 2500.50,
        cardType: 'MASTERCARD',
        isActive: true
      }
    ];
  }

  get currentCard(): CreditCard | null {
    return this.cards[this.currentCardIndex] || null;
  }

  nextCard() {
    if (this.currentCardIndex < this.cards.length - 1) {
      this.currentCardIndex++;
    }
  }

  previousCard() {
    if (this.currentCardIndex > 0) {
      this.currentCardIndex--;
    }
  }


  formatCardNumber(cardNumber: string): string {
    return `**** ${cardNumber.slice(-4)}`;
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  }

  getCardGradient(cardType: string): string {
    switch (cardType) {
      case 'VISA':
        return 'from-gradient-start to-gradient-end';
      case 'MASTERCARD':
        return 'from-red-500 to-orange-500';
      case 'AMEX':
        return 'from-blue-600 to-blue-800';
      default:
        return 'from-gradient-start to-gradient-end';
    }
  }

  getCardLogo(cardType: string): string {
    switch (cardType) {
      case 'VISA':
        return 'VISA';
      case 'MASTERCARD':
        return 'MC';
      case 'AMEX':
        return 'AMEX';
      default:
        return 'VISA';
    }
  }
}

