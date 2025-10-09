import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

export interface BankAccount {
  id: number;
  accountNumber: string;
  accountType: 'CHECKING' | 'SAVINGS';
  balance: number;
  agency: string;
  usuario?: any;
}

export interface CreateAccountRequest {
  balance: number;
}

export interface TransactionRequest {
  value: number;
}

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);
  private readonly baseUrl = 'http://localhost:8080/account';

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  getUserAccounts(): Observable<BankAccount[]> {
    return this.http.get<BankAccount[]>(`${this.baseUrl}`, {
      headers: this.getHeaders()
    });
  }

  createCheckingAccount(userId: number, balance: number): Observable<BankAccount> {
    const request: CreateAccountRequest = { balance };
    return this.http.post<BankAccount>(`${this.baseUrl}/checking/${userId}`, request, {
      headers: this.getHeaders()
    });
  }

  createSavingsAccount(userId: number, balance: number): Observable<BankAccount> {
    const request: CreateAccountRequest = { balance };
    return this.http.post<BankAccount>(`${this.baseUrl}/savings/${userId}`, request, {
      headers: this.getHeaders()
    });
  }

  deposit(accountNumber: string, value: number): Observable<BankAccount> {
    const request: TransactionRequest = { value };
    return this.http.post<BankAccount>(`${this.baseUrl}/deposit/${accountNumber}`, request, {
      headers: this.getHeaders()
    });
  }

  withdraw(accountNumber: string, value: number): Observable<BankAccount> {
    const request: TransactionRequest = { value };
    return this.http.post<BankAccount>(`${this.baseUrl}/withdraw/${accountNumber}`, request, {
      headers: this.getHeaders()
    });
  }

  getAccountByNumber(accountNumber: string): Observable<BankAccount> {
    return this.http.get<BankAccount>(`${this.baseUrl}/${accountNumber}`, {
      headers: this.getHeaders()
    });
  }
}

