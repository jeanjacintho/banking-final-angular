import { Component, inject, signal } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule, AbstractControl } from '@angular/forms';
import { CreditCardRequest, CreditCardRequestResponse, CreditCardRequestService } from '../../../services/credit-card-request.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CepService } from '../../../services/cep.service';
import { debounce, debounceTime, filter, map, switchMap, tap } from 'rxjs';

@Component({
  selector: 'app-credit-card-request',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './credit-card-request.html',
})
export class CreditCardRequestComponent {
  private fb = inject(FormBuilder);
  private svc = inject(CreditCardRequestService);
  private cepService = inject(CepService);

  loading = signal(false);
  result = signal<CreditCardRequestResponse | null>(null);

  loadingCep = signal(false);
  cepError = signal<string | null>(null);

  form = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(5)]],
    cpf: ['', [Validators.required, Validators.pattern(/^\d{3}\.\d{3}\.\d{3}-\d{2}$/)]],
    dateOfBirth: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    phoneNumber: ['', [Validators.required, Validators.pattern(/^\(\d{2}\) \d{4,5}-\d{4}$/)]],
    address: this.fb.group({
      cep: ['', [Validators.required, Validators.pattern(/^\d{5}-?\d{3}$/)]],
      logradouro: ['', [Validators.required]],
      numero: ['', [Validators.required]],
      complemento: [''],
      bairro: ['', [Validators.required]],
      cidade: ['', [Validators.required]],
      estado: ['', [Validators.required, Validators.maxLength(2), Validators.minLength(2)]],
    }),
    monthlyIncome: [null as number | null, [Validators.required, Validators.min(0)]],
    sourceIncome: ['CLT' as CreditCardRequest['fonteRenda'], [Validators.required]],
    company: [''],
    employmentTimeMonths: [null as number | null, [Validators.min(0)]],
    invoiceType: ['digital' as CreditCardRequest['tipoFatura'], [Validators.required]],
    preferredDueDate: [null as number | null, [Validators.min(1), Validators.max(28)]],
    acceptTerms: [false, [Validators.requiredTrue]],
    authorizationCreditConsultation: [false, [Validators.requiredTrue]],
  });

  ngOnInit() {
    const cepCtrl = this.form.get('address.cep')!;
    cepCtrl.valueChanges!.pipe(
      debounceTime(300),
      map(v => (v ?? '').toString().replace(/\D/g, '')),
      filter(v => v.length === 8),
      tap(() => { this.loadingCep.set(true); this.cepError.set(null); }),
      switchMap(cep => this.cepService.buscar(cep)),
      tap(() => this.loadingCep.set(false))
    ).subscribe(result => {
      if (!result) {
        this.cepError.set('CEP não encontrado');
        return;
      }
      this.form.patchValue({
        address: {
          logradouro: result.logradouro,
          complemento: result.complemento,
          bairro: result.bairro,
          cidade: result.cidade,
          estado: result.estado
        }
      });
    });
  }

  onCepBlur() {
    const ctrl = this.form.get('address.cep');
    const digits = (ctrl?.value || '').toString().replace(/\D/g, '');
    if (digits.length === 8) {
      ctrl?.setValue(digits.replace(/(\d{5})(\d{3})/, '$1-$2'), { emitEvent: false });
    }
  }

  isInvalid(ctrl: AbstractControl | null) {
    return !!ctrl && ctrl.invalid && (ctrl.dirty || ctrl.touched);
  }

  onSubmit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading.set(true);
    const payload = this.form.value as CreditCardRequest;
    this.svc.solicitarCartao(payload).subscribe({
      next: (res) => {
        this.result.set(res);
        this.loading.set(false);
      },
      error: (err) => {
        this.result.set({
          statusSolicitacao: 'pendente',
          mensagem: 'Não foi possível processar sua solicitação no momento. Tente novamente mais tarde.'
        });
        console.error(err);
        this.loading.set(false);
      }
    })
  }
}
