import { HttpClient } from "@angular/common/http";
import { inject, Inject, Injectable } from "@angular/core";
import { catchError, map, Observable, of } from "rxjs";

type ViaCepResponse = {
    cep: string;
    logradouro: string;
    complemento: string;
    bairro: string;
    localidade: string;
    uf: string;
    erro?: boolean;
};

type BrasilApiResponse = {
    cep: string;
    state: string;
    city: string;
    neighborhood: string;
    street: string;
    service?: string;
};

@Injectable({providedIn: 'root'})
export class CepService {
    private http = inject(HttpClient);

    private cleanCEP(cep: string){
        return (cep || '').replace(/\D/g, '').slice(0, 8);
    }

    buscar(cep: string): Observable<{
        cep: string;
        logradouro: string;
        complemento: string;
        bairro: string;
        cidade: string;
        estado: string;
    } | null> {
        const clean = this.cleanCEP(cep);
        if(clean.length !== 8) return of(null);

        return this.http.get<ViaCepResponse>(`https://viacep.com.br/ws/${clean}/json/`).pipe(
      map((res) => {
        if (!res || res.erro) throw new Error('CEP não encontrado');
        return {
          cep: res.cep,
          logradouro: res.logradouro ?? '',
          complemento: res.complemento ?? '',
          bairro: res.bairro ?? '',
          cidade: res.localidade ?? '',
          estado: res.uf ?? ''
        };
      }),
      catchError(() =>
        // Fallback: BrasilAPI
        this.http.get<BrasilApiResponse>(`https://brasilapi.com.br/api/cep/v1/${clean}`).pipe(
          map((b) => ({
            cep: b.cep,
            logradouro: b.street ?? '',
            complemento: '',
            bairro: b.neighborhood ?? '',
            cidade: b.city ?? '',
            estado: b.state ?? ''
          })),
          catchError(() => of(null))
        )
      )
    );
  }
}