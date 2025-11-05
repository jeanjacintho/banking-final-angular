import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { CreditCard } from '../models/credit-card.model';
import { environment } from '../../environments/environment';
import { CreditCardTransaction, CreateCreditCardTransactionRequest } from '../models/credit-card-transaction.model';

@Injectable({
  providedIn: 'root'
})
export class CreditCardService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiBase}/credit-cards`;

  getAllCards(): Observable<CreditCard[]> {
    return this.http.get<CreditCard[]>(`${this.apiUrl}/my-cards`);
  }

  addCard(card: CreditCard): Observable<CreditCard> {
    return this.http.post<CreditCard>(this.apiUrl, card);
  }

  deleteCard(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  updateCard(card: CreditCard): Observable<CreditCard> {
    return this.http.put<CreditCard>(`${this.apiUrl}/${card.id}`, card);
  }

  getCardById(id: number): Observable<CreditCard> {
    return this.http.get<CreditCard>(`${this.apiUrl}/${id}`);
  }

  getCardByNumber(cardNumber: string): Observable<CreditCard> {
    return this.http.get<CreditCard>(`${this.apiUrl}/number/${cardNumber}`);
  }

  listTransactions(cardId: number): Observable<CreditCardTransaction[]> {
    return this.http.get<CreditCardTransaction[]>(`${this.apiUrl}/${cardId}/transactions`);
  }

  createTransaction(cardId: number, req: CreateCreditCardTransactionRequest): Observable<CreditCardTransaction> {
    return this.http.post<CreditCardTransaction>(`${this.apiUrl}/${cardId}/transactions`, req);
  }

  getCvv(cardId: number): Observable<{ cvv: string | null; error?: string }> {
    return this.http.get<{ cvv: string | null; error?: string }>(`${this.apiUrl}/${cardId}/cvv`);
  }
}
