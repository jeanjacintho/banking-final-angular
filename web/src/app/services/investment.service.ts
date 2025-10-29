import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';

export interface CreateCdbRequest {
  accountNumber: string;
  term: number; // meses
  value: number;
}

export interface CreateRendaFixaRequest {
  accountNumber: string;
  value: number;
}

export interface InvestmentResponse {
  id: number;
  investmentType: string;
  investmentValue: number;
  investmentTerm?: number;
  currentTerm?: number;
  active: boolean;
}

@Injectable({ providedIn: 'root' })
export class InvestmentService {
  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);
  private readonly baseUrl = `${environment.apiBase}/investment`;

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  createCdb(req: CreateCdbRequest): Observable<InvestmentResponse> {
    const body = { term: req.term, value: req.value };
    return this.http.post<InvestmentResponse>(`${this.baseUrl}/cdb/${req.accountNumber}`, body, { headers: this.getHeaders() });
  }

  createRendaFixa(req: CreateRendaFixaRequest): Observable<InvestmentResponse> {
    const body = { value: req.value };
    return this.http.post<InvestmentResponse>(`${this.baseUrl}/renda-fixa/${req.accountNumber}`, body, { headers: this.getHeaders() });
  }

  getMyInvestments(): Observable<InvestmentResponse[]> {
    return this.http.get<InvestmentResponse[]>(`${this.baseUrl}/my`, { headers: this.getHeaders() });
  }
}
