import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export type LoanType = 'PERSONAL' | 'CONSIGNED';

export type LoanStatus = 'EM_ANALISE' | 'APROVADO' | 'REPROVADO' | 'ATIVO' | 'QUITADO' | 'INADIMPLENTE';

export interface LoanRequest {
  totalAmount: number;
  interestRate: number;
  numberOfInstallments: number;
  type: LoanType;
  userId: number;
}

export interface LoanSimulation {
  requestedAmount: number;
  totalAmount: number;
  monthlyInstallment: number;
  totalInterest: number;
  monthlyRate: number;
  numberOfInstallments: number;
  loanType: LoanType;
}

export interface LoanResponse {
  id: number;
  totalAmount: number;
  interestRate: number;
  monthlyInstallment: number;
  numberOfInstallments: number;
  startDate: string;
  type: LoanType;
  status: LoanStatus;
  installments?: InstallmentDTO[];
}

export interface InstallmentDTO {
  id: number;
  number: number;
  dueDate: string;
  amount: number;
  status: string;
}

@Injectable({
  providedIn: 'root'
})
export class LoanService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBase}/loans`;

  simulateLoan(request: LoanRequest): Observable<LoanSimulation> {
    return this.http.post<LoanSimulation>(`${this.baseUrl}/simulate`, request);
  }

  requestLoan(request: LoanRequest): Observable<LoanResponse> {
    return this.http.post<LoanResponse>(`${this.baseUrl}/request`, request);
  }

  getUserLoans(userId?: number): Observable<LoanResponse[]> {
    const params = userId ? `?userId=${userId}` : '';
    return this.http.get<LoanResponse[]>(`${this.baseUrl}${params}`);
  }

  getLoanById(id: number): Observable<LoanResponse> {
    return this.http.get<LoanResponse>(`${this.baseUrl}/${id}`);
  }
}

