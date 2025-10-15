import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LucideAngularModule } from 'lucide-angular';
import { CurrencyService, CurrencyConversionRequest, CurrencyConversionResponse, SupportedCurrenciesResponse } from '../../services/currency.service';

@Component({
  selector: 'app-currency-converter',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  templateUrl: './currency-converter.html',
  styleUrls: ['./currency-converter.css']
})
export class CurrencyConverterComponent implements OnInit {
  private readonly currencyService = inject(CurrencyService);

  amount: number = 100;
  fromCurrency: string = 'USD';
  toCurrency: string = 'BRL';

  conversionResult: CurrencyConversionResponse | null = null;
  supportedCurrencies: string[] = [];
  
  loading: boolean = false;
  error: string | null = null;

  ngOnInit() {
    this.loadSupportedCurrencies();
  }

  private loadSupportedCurrencies() {
    this.currencyService.getSupportedCurrencies().subscribe({
      next: (response: SupportedCurrenciesResponse) => {
        this.supportedCurrencies = response.supportedCurrencies;
      },
      error: (error) => {
        console.error('Error loading supported currencies:', error);
        this.supportedCurrencies = ['USD', 'EUR', 'BRL', 'GBP', 'JPY'];
      }
    });
  }

  convertCurrency() {
    if (!this.amount || this.amount <= 0) {
      this.error = 'Por favor, insira um valor válido';
      return;
    }

    if (this.fromCurrency === this.toCurrency) {
      this.error = 'Selecione moedas diferentes';
      return;
    }

    this.loading = true;
    this.error = null;
    this.conversionResult = null;

    const request: CurrencyConversionRequest = {
      amount: this.amount,
      fromCurrency: this.fromCurrency,
      toCurrency: this.toCurrency
    };

    this.currencyService.convertCurrency(request).subscribe({
      next: (response: CurrencyConversionResponse) => {
        this.conversionResult = response;
        this.loading = false;
      },
      error: (error) => {
        this.error = error.error?.message || 'Erro ao converter moeda';
        this.loading = false;
      }
    });
  }

  swapCurrencies() {
    const temp = this.fromCurrency;
    this.fromCurrency = this.toCurrency;
    this.toCurrency = temp;
    this.conversionResult = null;
    this.error = null;
  }

  formatCurrency(amount: number, currency: string): string {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: currency
    }).format(amount);
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleString('pt-BR');
  }

  isRealTimeRate(): boolean {
    return this.conversionResult?.provider?.includes('Real-time') || false;
  }
}

