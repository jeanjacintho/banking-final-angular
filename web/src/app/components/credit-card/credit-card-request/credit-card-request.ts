import { Component, inject, signal, OnInit } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule, AbstractControl } from '@angular/forms';
import { CreditCardRequest, CreditCardRequestResponse, CreditCardRequestService } from '../../../services/credit-card-request.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CepService } from '../../../services/cep.service';
import { AuthService } from '../../../services/auth.service';
import { debounce, debounceTime, filter, map, switchMap, tap } from 'rxjs';
import { Layout } from '../../layout/layout';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-credit-card-request',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, Layout, LucideAngularModule],
  templateUrl: './credit-card-request.html',
})
export class CreditCardRequestComponent implements OnInit {
  private fb = inject(FormBuilder);
  private svc = inject(CreditCardRequestService);
  private cepService = inject(CepService);
  private authService = inject(AuthService);

  loading = signal(false);
  result = signal<CreditCardRequestResponse | null>(null);

  loadingCep = signal(false);
  cepError = signal<string | null>(null);
  loadingUserData = signal(false);

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
    sourceIncome: ['CLT' as 'CLT' | 'PJ' | 'Autonomo' | 'Aposentado' | 'Estudante' | 'Outros', [Validators.required]],
    company: [''],
    employmentTimeMonths: [null as number | null, [Validators.min(0)]],
    invoiceType: ['digital' as 'digital' | 'papel', [Validators.required]],
    preferredDueDate: [null as number | null, [Validators.min(1), Validators.max(28)]],
    acceptTerms: [false, [Validators.requiredTrue]],
    authorizationCreditConsultation: [false, [Validators.requiredTrue]],
  });

  ngOnInit() {
    this.loadUserData();
    this.setupCepAutoFill();
  }

  loadUserData() {
    this.loadingUserData.set(true);
    this.authService.getUserInfo().subscribe({
      next: (user) => {
        if (user) {
          this.prefillFormFromUserData(user);
        }
        this.loadingUserData.set(false);
      },
      error: (error) => {
        console.error('Erro ao carregar dados do usuário:', error);
        this.loadingUserData.set(false);
      }
    });
  }

  private prefillFormFromUserData(user: any) {
    // Formatar CPF para o formato do formulário (000.000.000-00)
    const cpf = user.cpf?.replace(/\D/g, '');
    const formattedCpf = cpf?.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
    
    // Formatar telefone se existir
    let phone = user.telefone || '';
    if (phone) {
      phone = phone.replace(/\D/g, '');
      if (phone.length === 10) {
        phone = phone.replace(/(\d{2})(\d{4})(\d{4})/, '($1) $2-$3');
      } else if (phone.length === 11) {
        phone = phone.replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3');
      }
    }

    // Formatar data de nascimento
    let birthDate = '';
    if (user.dataNascimento) {
      const date = new Date(user.dataNascimento);
      birthDate = date.toISOString().split('T')[0];
    }

    // Extrair dados do endereço completo
    const addressData = this.parseEnderecoCompleto(user.enderecoCompleto || '');

    // Atualizar formulário com dados do usuário
    this.form.patchValue({
      name: user.nomeCompleto || '',
      cpf: formattedCpf || '',
      email: user.email || '',
      phoneNumber: phone,
      dateOfBirth: birthDate,
      monthlyIncome: user.income ? Number(user.income) : null,
      address: {
        logradouro: addressData.logradouro,
        numero: addressData.numero,
        complemento: addressData.complemento,
        bairro: addressData.bairro,
        cidade: addressData.cidade,
        estado: addressData.estado,
        cep: addressData.cep
      }
    });
  }

  private parseEnderecoCompleto(endereco: string): any {
    // Se não há endereço, retornar campos vazios
    if (!endereco) {
      return {
        logradouro: '',
        numero: '',
        complemento: '',
        bairro: '',
        cidade: '',
        estado: '',
        cep: ''
      };
    }

    // Tentar identificar componentes do endereço em vários formatos
    let logradouro = '';
    let numero = '';
    let complemento = '';
    let bairro = '';
    let cidade = '';
    let estado = '';
    let cep = '';

    // Extrair CEP (formato: 12345-678 ou 12345678)
    const cepMatch = endereco.match(/\d{5}-?\d{3}/);
    if (cepMatch) {
      cep = cepMatch[0].replace(/\D/g, '').replace(/(\d{5})(\d{3})/, '$1-$2');
    }

    // Extrair Estado (formato: /SP ou SP)
    const stateMatch = endereco.match(/\/\s*([A-Z]{2})|([A-Z]{2})\s*$/);
    if (stateMatch) {
      estado = stateMatch[1] || stateMatch[2] || '';
    }

    // Remover CEP e estado para processar o resto
    let cleanAddress = endereco
      .replace(/\d{5}-?\d{3}/g, '')
      .replace(/\/\s*[A-Z]{2}/g, '')
      .trim();

    // Separar por vírgulas ou hífens
    const parts = cleanAddress.split(/[,-]/).map(s => s.trim()).filter(s => s);
    
    if (parts.length > 0) {
      // Primeira parte normalmente é o logradouro
      logradouro = parts[0];
      
      // Segunda parte pode ser número
      if (parts.length > 1 && parts[1].match(/\d/)) {
        numero = parts[1];
      }
      
      // Procurar bairro (geralmente antes da cidade)
      const bairroIndex = parts.findIndex(p => p.match(/centro|zona|leste|oeste|norte|sul|vila|jardim|parque/i));
      if (bairroIndex !== -1) {
        bairro = parts[bairroIndex];
      }
      
      // Procurar cidade (geralmente a última ou penúltima parte)
      const cityPattern = /^(São Paulo|Rio de Janeiro|Belo Horizonte|Salvador|Brasília|Curitiba|Recife|Fortaleza|Manaus|Belém|Porto Alegre|[A-Z][a-z]+)$/i;
      const cityMatch = parts.find(p => cityPattern.test(p));
      if (cityMatch) {
        cidade = cityMatch;
      }
    }

    return {
      logradouro,
      numero,
      complemento,
      bairro,
      cidade,
      estado,
      cep
    };
  }

  setupCepAutoFill() {
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
    
    // Mapear formulário para payload da API
    const formValue = this.form.value;
    const payload: CreditCardRequest = {
      name: formValue.name || '',
      cpf: formValue.cpf || '',
      dateOfBirth: formValue.dateOfBirth || '',
      email: formValue.email || '',
      phoneNumber: formValue.phoneNumber || '',
      address: {
        logradouro: formValue.address?.logradouro || '',
        numero: formValue.address?.numero || '',
        complemento: formValue.address?.complemento || '',
        bairro: formValue.address?.bairro || '',
        cidade: formValue.address?.cidade || '',
        estado: formValue.address?.estado || '',
        cep: formValue.address?.cep || ''
      },
      monthlyIncome: formValue.monthlyIncome || 0,
      sourceIncome: formValue.sourceIncome || 'CLT',
      company: formValue.company || '',
      employmentTimeMonths: formValue.employmentTimeMonths || undefined,
      invoiceType: formValue.invoiceType || 'digital',
      preferredDueDate: formValue.preferredDueDate || undefined,
      acceptTerms: formValue.acceptTerms || false,
      authorizationCreditConsultation: formValue.authorizationCreditConsultation || false
    };
    
    this.svc.solicitarCartao(payload).subscribe({
      next: (res) => {
        this.result.set(res);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Erro ao solicitar cartão:', err);
        this.result.set({
          statusSolicitacao: 'pendente',
          mensagem: 'Não foi possível processar sua solicitação no momento. Tente novamente mais tarde.'
        });
        this.loading.set(false);
      }
    })
  }
}
