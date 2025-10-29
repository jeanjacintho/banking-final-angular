import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LucideAngularModule } from 'lucide-angular';
import { RouterModule } from '@angular/router';
import { CreditCardService } from '../../services/credit-card-service';
import { CreditCard } from '../../models/credit-card.model';

interface DisplayCard {
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
  imports: [CommonModule, FormsModule, LucideAngularModule, RouterModule],
  templateUrl: './credit-card.html'
})
export class CreditCardComponent implements OnInit {
  private creditCardService = inject(CreditCardService);
  
  cards: DisplayCard[] = [];
  currentCardIndex = 0;
  isLoading = false;
  errorMessage = '';

  ngOnInit() {
    this.loadCards();
  }

  loadCards() {
    this.isLoading = true;
    this.errorMessage = '';
    
    this.creditCardService.getAllCards().subscribe({
      next: (apiCards) => {
        console.log('Cartões recebidos:', apiCards);
        this.cards = apiCards.map(card => this.mapApiCardToDisplay(card));
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Erro ao carregar cartões:', error);
        this.errorMessage = `Erro ao carregar cartões: ${error.message}`;
        this.isLoading = false;
      }
    });
  }

  private mapApiCardToDisplay(apiCard: CreditCard): DisplayCard {
    // Mapear formato da API para formato de exibição
    const cardType = this.getCardTypeFromBrand(apiCard.brand);
    
    // Formatar data de expiração
    const expMonth = String(apiCard.expMonth || 1).padStart(2, '0');
    const expYear = String(apiCard.expYear || 24).slice(-2);
    const expiryDate = `${expMonth}/${expYear}`;
    
    return {
      id: apiCard.id,
      cardNumber: apiCard.maskedPan || apiCard.cardNumber,
      cardHolder: apiCard.cardHolderName,
      expiryDate: expiryDate,
      balance: Number(apiCard.availableLimit),
      cardType: cardType,
      isActive: apiCard.isActive
    };
  }

  private getCardTypeFromBrand(brand: string): 'VISA' | 'MASTERCARD' | 'AMEX' {
    const brandUpper = brand?.toUpperCase() || '';
    if (brandUpper.includes('VISA')) return 'VISA';
    if (brandUpper.includes('MASTERCARD')) return 'MASTERCARD';
    if (brandUpper.includes('AMEX')) return 'AMEX';
    return 'VISA'; // default
  }

  get currentCard(): DisplayCard | null {
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

