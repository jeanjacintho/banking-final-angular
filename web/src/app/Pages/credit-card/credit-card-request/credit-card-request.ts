import { Component, inject, signal } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule, AbstractControl } from '@angular/forms';
import { CreditCardRequest, CreditCardRequestResponse, CreditCardRequestService } from '../../../services/credit-card-request.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-credit-card-request',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './credit-card-request.html',
})
export class CreditCardRequestComponent {
  private fb = inject(FormBuilder);
  private svc = inject(CreditCardRequestService);

  loading = signal(false);
  result = signal<CreditCardRequestResponse | null>(null);

  form = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(5)]],
    cpf: ['', [Validators.required, Validators.pattern(/^\d{3}\.\d{3}\.\d{3}-\d{2}$/)]],
    dateOfBirth: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    phoneNumber: ['', [Validators.required, Validators.pattern(/^\(\d{2}\) \d{4,5}-\d{4}$/)]],
    address: this.fb.group({
      logradouro: ['', [Validators.required]],
      numero: ['', [Validators.required]],
      complemento: [''],
      bairro: ['', [Validators.required]],
      cidade: ['', [Validators.required]],
      estado: ['', [Validators.required, Validators.maxLength(2), Validators.minLength(2)]],
      cep: ['', [Validators.required, Validators.pattern(/^\d{5}-\d{3}$/)]],
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
