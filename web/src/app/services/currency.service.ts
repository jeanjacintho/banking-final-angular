import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface CurrencyConversionRequest {
  amount: number;
  fromCurrency: string;
  toCurrency: string;
}

export interface CurrencyConversionResponse {
  originalAmount: number;
  fromCurrency: string;
  convertedAmount: number;
  toCurrency: string;
  exchangeRate: number;
  conversionDate: string;
  provider: string;
}

export interface SupportedCurrenciesResponse {
  supportedCurrencies: string[];
  totalCount: number;
}

@Injectable({
  providedIn: 'root'
})
export class CurrencyService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiBase;

  convertCurrency(request: CurrencyConversionRequest): Observable<CurrencyConversionResponse> {
    return this.http.post<CurrencyConversionResponse>(`${this.apiUrl}/currency/convert`, request);
  }

  getSupportedCurrencies(): Observable<SupportedCurrenciesResponse> {
    return this.http.get<SupportedCurrenciesResponse>(`${this.apiUrl}/currency/supported`);
  }
}
