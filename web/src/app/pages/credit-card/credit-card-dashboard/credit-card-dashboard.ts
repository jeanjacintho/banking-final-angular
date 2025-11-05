import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { InvoiceSummaryComponent } from '../../../components/credit-card/invoice-summary/invoice-summary';
import { CreditCardService } from '../../../services/credit-card-service';
import { CreditCard } from '../../../models/credit-card.model';
import { RouterModule } from '@angular/router';
import { Layout } from '../../../components/layout/layout';
import { LucideAngularModule } from 'lucide-angular';
import { CreditCardTransaction } from '../../../models/credit-card-transaction.model';
import { CreditCardHistoryComponent } from '../../../components/credit-card/credit-card-history/credit-card-history';

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
    InvoiceSummaryComponent,
    RouterModule,
    Layout,
    LucideAngularModule,
    CreditCardHistoryComponent,
  ],
})
export class CreditCardDashboardComponent implements OnInit {
  constructor(private creditCardService: CreditCardService) {}

  cards: CreditCard[] = [];
  totalLimit: number = 0;
  currentCardIndex = 0;
  showFullNumber = false;
  loadingCvv = false;
  cvvError: string | null = null;

  selectedCard: CreditCard | null = null;
  invoiceLoading = false;
  invoiceItems: InvoiceItem[] = [];

  ngOnInit(): void {
    this.creditCardService.getAllCards().subscribe({
      next: (cards) => {
        console.log('Cartões recebidos no dashboard:', cards);
        this.cards = cards;
        this.totalLimit = this.cards.reduce((sum, card) => sum + card.creditLimit, 0);

        // Opcional: já seleciona o primeiro card
        if (this.cards.length && !this.selectedCard) {
          this.currentCardIndex = 0;
          this.onSelectCard(this.cards[0]);
        }
      },
      error: (error) => {
        console.error('Erro ao carregar cartões no dashboard:', error);
      }
    });
  }

  onSelectCard(card: CreditCard) {
    if (this.selectedCard?.id === card.id) return;
    this.selectedCard = card;
    this.loadInvoice(card.id);
  }

  // Método público para recarregar os dados (pode ser chamado externamente)
  refresh() {
    if (this.selectedCard) {
      this.loadInvoice(this.selectedCard.id);
    } else if (this.cards.length > 0) {
      this.onSelectCard(this.cards[0]);
    }
  }

  private loadInvoice(cardId: number) {
    this.invoiceLoading = true;
    
    // Busca transações e cartão atualizado em paralelo
    this.creditCardService.listTransactions(cardId).subscribe({
      next: (txs: CreditCardTransaction[]) => {
        this.invoiceItems = txs.map((t) => {
          const amount = typeof t.amount === 'number' ? t.amount : parseFloat(String(t.amount));
          return {
            date: t.createdAt,
            description: `${t.merchantName}${t.installmentsTotal ? ` (${t.installmentsTotal}x)` : ''}`,
            amount: -Math.abs(amount),
          };
        });
        
        // Atualiza o cartão selecionado com os dados mais recentes (limite disponível)
        this.refreshSelectedCard();
      },
      error: () => {
        this.invoiceItems = [];
        this.invoiceLoading = false;
      }
    });
  }

  private refreshSelectedCard() {
    if (!this.selectedCard) {
      this.invoiceLoading = false;
      return;
    }
    
    // Busca o cartão atualizado do servidor para obter o limite disponível atualizado
    this.creditCardService.getCardById(this.selectedCard.id).subscribe({
      next: (updatedCard) => {
        // Atualiza o cartão na lista
        const index = this.cards.findIndex(c => c.id === updatedCard.id);
        if (index !== -1) {
          this.cards[index] = updatedCard;
        }
        
        // Atualiza o cartão selecionado
        this.selectedCard = updatedCard;
        this.invoiceLoading = false;
      },
      error: (error) => {
        console.error('Erro ao atualizar cartão:', error);
        this.invoiceLoading = false;
      }
    });
  }

  get invoiceTotalBySelected(): number {
    return this.invoiceItems
      .filter((tx) => tx.amount < 0)
      .reduce((sum, tx) => sum + Math.abs(tx.amount), 0);
  }

  get selectedCardCreditLimit(): number {
    return this.selectedCard?.creditLimit || 0;
  }

  get selectedCardAvailableLimit(): number {
    return this.selectedCard?.availableLimit || 0;
  }

  get currentCard(): CreditCard | null {
    return this.cards[this.currentCardIndex] || null;
  }

  nextCard() {
    if (this.currentCardIndex < this.cards.length - 1) {
      this.currentCardIndex++;
      this.onSelectCard(this.cards[this.currentCardIndex]);
    }
  }

  previousCard() {
    if (this.currentCardIndex > 0) {
      this.currentCardIndex--;
      this.onSelectCard(this.cards[this.currentCardIndex]);
    }
  }

  formatCardNumber(cardNumber: string): string {
    if (!cardNumber) return '**** **** **** ****';
    const last4 = cardNumber.slice(-4);
    return `**** ${last4}`;
  }

  formatFullCardNumber(cardNumber: string): string {
    if (!cardNumber) return '**** **** **** ****';
    return String(cardNumber)
      .replace(/\s+/g, '')
      .replace(/(.{4})/g, '$1 ')
      .trim();
  }

  toggleShowNumber() {
    this.showFullNumber = !this.showFullNumber;
    
    // Se está mostrando o número e ainda não tem CVV, buscar o CVV
    const currentCard = this.currentCard;
    if (this.showFullNumber && currentCard && !currentCard.cvv && !this.loadingCvv && !this.cvvError) {
      this.loadCvv(currentCard);
    }
    
    // Se está ocultando, limpar o CVV e erro
    if (!this.showFullNumber) {
      if (currentCard) {
        currentCard.cvv = undefined;
      }
      this.cvvError = null;
      this.loadingCvv = false;
    }
  }

  private loadCvv(card: CreditCard) {
    if (!card.id) return;
    
    this.loadingCvv = true;
    this.cvvError = null;
    
    this.creditCardService.getCvv(card.id).subscribe({
      next: (response) => {
        if (response.cvv) {
          card.cvv = response.cvv;
        } else if (response.error) {
          card.cvv = undefined;
          this.cvvError = response.error;
        }
        this.loadingCvv = false;
      },
      error: (error) => {
        console.error('Erro ao buscar CVV:', error);
        if (error.status === 404) {
          this.cvvError = 'CVV não disponível para este cartão';
        } else {
          this.cvvError = 'Erro ao buscar CVV';
        }
        card.cvv = undefined;
        this.loadingCvv = false;
      }
    });
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  }

  getCardLogo(brand: string): string {
    const brandUpper = brand?.toUpperCase() || '';
    if (brandUpper.includes('VISA')) return 'VISA';
    if (brandUpper.includes('MASTERCARD')) return 'MC';
    if (brandUpper.includes('AMEX')) return 'AMEX';
    return 'CARD';
  }

  formatExpiryDate(card: CreditCard): string {
    if (!card) return '';
    const month = card.expMonth ? String(card.expMonth).padStart(2, '0') : '00';
    const year = card.expYear ? String(card.expYear).slice(-2) : '00';
    return `${month}/${year}`;
  }

  selectCardByIndex(index: number, card: CreditCard) {
    this.currentCardIndex = index;
    this.onSelectCard(card);
  }
}

