import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';
import { environment } from '../../environments/environment';

export interface PixKey {
  id: number;
  keyType: 'CPF' | 'EMAIL' | 'PHONE';
  keyValue: string;
  createdAt: string;
}

export interface CreatePixKeyRequest {
  accountId: number;
  keyType: 'CPF' | 'EMAIL' | 'PHONE';
  keyValue: string;
}

export interface CreatePixKeyResponse {
  success: boolean;
  message: string;
  pixKey?: {
    id: number;
    keyType: string;
    keyValue: string;
  };
}

export interface PixKeysResponse {
  success: boolean;
  keys: PixKey[];
  total: number;
  message?: string;
}

export interface DeletePixKeyResponse {
  success: boolean;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class PixService {
  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);
  private readonly baseUrl = `${environment.apiBase}/pix`;

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  createPixKey(request: CreatePixKeyRequest): Observable<CreatePixKeyResponse> {
    return this.http.post<CreatePixKeyResponse>(`${this.baseUrl}/keys`, request, {
      headers: this.getHeaders()
    });
  }

  getPixKeysByAccount(accountId: number): Observable<PixKeysResponse> {
    return this.http.get<PixKeysResponse>(`${this.baseUrl}/keys?accountId=${accountId}`, {
      headers: this.getHeaders()
    });
  }

  deletePixKey(keyId: number): Observable<DeletePixKeyResponse> {
    return this.http.delete<DeletePixKeyResponse>(`${this.baseUrl}/keys/${keyId}`, {
      headers: this.getHeaders()
    });
  }

  deletePixKeyByTypeAndValue(keyType: string, keyValue: string): Observable<DeletePixKeyResponse> {
    const request = { keyType, keyValue };
    return this.http.post<DeletePixKeyResponse>(`${this.baseUrl}/keys/delete`, request, {
      headers: this.getHeaders()
    });
  }
}



