import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';
import { environment } from '../../environments/environment';

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

export interface TransferRequest {
  fromAccount: string;
  toAccount: string;
  amount: number;
  type: string;
}

export interface TransferResponse {
  fromAccount: string;
  toAccount: string;
  amount: number;
  type: string;
  fromBalanceAfter: number;
  toBalanceAfter: number;
  message: string;
}

export interface TransactionHistoryItem {
  id: number;
  fromAccount: BankAccount | null;
  toAccount: BankAccount | null;
  amount: number;
  type: 'INTERNAL' | 'TED' | 'PIX' | 'DEPOSIT' | 'WITHDRAW' | 'CREDIT';
  timestamp: string;
}

@Injectable({
  providedIn: 'root',
})
export class AccountService {
  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);
  private readonly baseUrl = `${environment.apiBase}/account`;

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    });
  }

  getUserAccounts(): Observable<BankAccount[]> {
    return this.http.get<BankAccount[]>(`${this.baseUrl}/my-accounts`, {
      headers: this.getHeaders(),
    });
  }

  createCheckingAccount(userId: number, balance: number): Observable<BankAccount> {
    const request: CreateAccountRequest = { balance };
    return this.http.post<BankAccount>(`${this.baseUrl}/checking/${userId}`, request, {
      headers: this.getHeaders(),
    });
  }

  createSavingsAccount(userId: number, balance: number): Observable<BankAccount> {
    const request: CreateAccountRequest = { balance };
    return this.http.post<BankAccount>(`${this.baseUrl}/savings/${userId}`, request, {
      headers: this.getHeaders(),
    });
  }

  deposit(accountNumber: string, value: number): Observable<BankAccount> {
    const request: TransactionRequest = { value };
    return this.http.post<BankAccount>(`${this.baseUrl}/deposit/${accountNumber}`, request, {
      headers: this.getHeaders(),
    });
  }

  withdraw(accountNumber: string, value: number): Observable<BankAccount> {
    const request: TransactionRequest = { value };
    return this.http.post<BankAccount>(`${this.baseUrl}/withdraw/${accountNumber}`, request, {
      headers: this.getHeaders(),
    });
  }

  getAccountByNumber(accountNumber: string): Observable<BankAccount> {
    return this.http.get<BankAccount>(`${this.baseUrl}/${accountNumber}`, {
      headers: this.getHeaders(),
    });
  }

  transfer(transferRequest: TransferRequest): Observable<TransferResponse> {
    return this.http.post<TransferResponse>(`${this.baseUrl}/transfer`, transferRequest, {
      headers: this.getHeaders(),
    });
  }

  transferCredit(transferRequest: TransferRequest): Observable<TransferResponse> {
    return this.http.post<TransferResponse>(`${this.baseUrl}/transfer/credit`, transferRequest, {
      headers: this.getHeaders(),
    });
  }

  getMyTransactions(): Observable<TransactionHistoryItem[]> {
    return this.http.get<TransactionHistoryItem[]>(`${this.baseUrl}/my-transactions`, {
      headers: this.getHeaders(),
    });
  }
}
