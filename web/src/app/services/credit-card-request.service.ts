import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../environments/environment";

export interface CreditCardRequest {
  name: string;
  cpf: string;
  dateOfBirth: string; // ISO YYYY-MM-DD
  email: string;
  phoneNumber: string;
  address: {
    logradouro: string;
    numero: string;
    complemento?: string;
    bairro: string;
    cidade: string;
    estado: string;
    cep: string;
  };
  monthlyIncome: number;
  sourceIncome: 'CLT' | 'PJ' | 'Autonomo' | 'Aposentado' | 'Estudante' | 'Outros';
  company?: string;
  employmentTimeMonths?: number;
  invoiceType?: 'digital' | 'papel';
  preferredDueDate?: number;
  acceptTerms: boolean;
  authorizationCreditConsultation: boolean;
}

export interface CreditCardRequestResponse {
  statusSolicitacao: 'aprovado' | 'pendente' | 'recusado';
  limiteAprovado?: number;
  brand?: 'Visa';
  maskedPan?: string;       // ex: **** **** **** 1234
  cardToken?: string;       // token seguro para operações futuras
  dataVencimentoFatura?: string; // dia/mês ou ISO
  cvv?: string;            // CVV do cartão (apenas na criação)
  pendencias?: string[];    // documentos pendentes, etc.
  mensagem?: string;
}

@Injectable({
  providedIn: 'root'
})
export class CreditCardRequestService {
    private http = inject(HttpClient);
    private baseUrl = `${environment.apiBase}/credit-cards`;

    solicitarCartao(payload: CreditCardRequest): Observable<CreditCardRequestResponse> {
        return this.http.post<CreditCardRequestResponse>(`${this.baseUrl}/requests`, payload);
    }
}