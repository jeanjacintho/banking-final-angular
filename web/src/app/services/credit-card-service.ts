import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CreditCard } from '../models/credit-card.model';

@Injectable({
  providedIn: 'root'
})
export class CreditCardService {
  private apiUrl = 'http://localhost:8081/api/credit-cards'; // URL da API

  constructor(private http: HttpClient){ }

  getAllCards(): Observable<CreditCard[]> {
    return this.http.get<CreditCard[]>(this.apiUrl);
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
}
