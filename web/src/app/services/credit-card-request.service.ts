import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";

export interface CreditCardRequest {
  nomeCompleto: string;
  cpf: string;
  dataNascimento: string; // ISO YYYY-MM-DD
  email: string;
  telefone: string;
  endereco: {
    logradouro: string;
    numero: string;
    complemento?: string;
    bairro: string;
    cidade: string;
    estado: string;
    cep: string;
  };
  rendaMensal: number;
  fonteRenda: 'CLT' | 'PJ' | 'Autonomo' | 'Aposentado' | 'Estudante' | 'Outros';
  empresa?: string;
  tempoEmpregoMeses?: number;
  despesasMensaisAproximadas?: number;
  tipoFatura?: 'digital' | 'papel';
  diaVencimentoPreferido?: number;
  aceiteTermos: boolean;
  autorizacaoConsultaCredito: boolean;
}

export interface CreditCardRequestResponse {
  statusSolicitacao: 'aprovado' | 'pendente' | 'recusado';
  limiteAprovado?: number;
  brand?: 'Visa';
  maskedPan?: string;       // ex: **** **** **** 1234
  cardToken?: string;       // token seguro para operações futuras
  dataVencimentoFatura?: string; // dia/mês ou ISO
  pendencias?: string[];    // documentos pendentes, etc.
  mensagem?: string;
}

@Injectable({
  providedIn: 'root'
})
export class CreditCardRequestService {
    private http = inject(HttpClient);
    private baseUrl = 'https://localhost:8080/api/credit-cards';

    solicitarCartao(payload: CreditCardRequest): Observable<CreditCardRequestResponse> {
        return this.http.post<CreditCardRequestResponse>(`${this.baseUrl}/requests`, payload);
    }
}