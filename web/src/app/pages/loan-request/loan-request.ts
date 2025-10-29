import { Component, inject, signal, OnInit } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule, AbstractControl } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Layout } from '../../components/layout/layout';
import { LucideAngularModule } from 'lucide-angular';
import { LoanService, LoanType, LoanSimulation, LoanResponse } from '../../services/loan.service';
import { AuthService } from '../../services/auth.service';
import { catchError, of } from 'rxjs';

@Component({
  selector: 'app-loan-request',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, Layout, LucideAngularModule],
  templateUrl: './loan-request.html',
  styleUrl: './loan-request.css'
})
export class LoanRequestComponent implements OnInit {
  private fb = inject(FormBuilder);
  private loanService = inject(LoanService);
  private authService = inject(AuthService);

  loading = signal(false);
  simulating = signal(false);
  simulation = signal<LoanSimulation | null>(null);
  loanResult = signal<LoanResponse | null>(null);
  error = signal<string | null>(null);
  userId = signal<number | null>(null);

  loanTypes: { value: LoanType; label: string; description: string }[] = [
    {
      value: 'PERSONAL',
      label: 'Empréstimo Pessoal',
      description: 'R$ 1.000 a R$ 50.000, 6 a 48 meses, taxa de 2,5% ao mês'
    },
    {
      value: 'CONSIGNED',
      label: 'Empréstimo Consignado',
      description: 'R$ 2.000 a R$ 100.000, 12 a 84 meses, taxa de 1,5% ao mês'
    }
  ];

  form = this.fb.group({
    type: ['PERSONAL' as LoanType, [Validators.required]],
    totalAmount: [null as number | null, [Validators.required, Validators.min(0)]],
    numberOfInstallments: [null as number | null, [Validators.required, Validators.min(1)]]
  });

  ngOnInit() {
    this.loadUserInfo();
    this.setupFormValidation();
  }

  loadUserInfo() {
    this.authService.getUserInfo().subscribe({
      next: (user) => {
        if (user?.id) {
          this.userId.set(user.id);
        }
      },
      error: (err) => {
        console.error('Erro ao carregar informações do usuário:', err);
        this.error.set('Não foi possível carregar suas informações. Tente fazer login novamente.');
      }
    });
  }

  setupFormValidation() {
    this.form.get('type')?.valueChanges.subscribe(() => {
      this.updateFormValidators();
      this.simulation.set(null);
      this.loanResult.set(null);
      this.error.set(null);
    });

    this.form.get('totalAmount')?.valueChanges.subscribe(() => {
      this.simulation.set(null);
      this.loanResult.set(null);
    });

    this.form.get('numberOfInstallments')?.valueChanges.subscribe(() => {
      this.simulation.set(null);
      this.loanResult.set(null);
    });

    this.updateFormValidators();
  }

  updateFormValidators() {
    const type = this.form.get('type')?.value;
    const amountControl = this.form.get('totalAmount');
    const installmentsControl = this.form.get('numberOfInstallments');

    if (type === 'PERSONAL') {
      amountControl?.setValidators([
        Validators.required,
        Validators.min(1000),
        Validators.max(50000)
      ]);
      installmentsControl?.setValidators([
        Validators.required,
        Validators.min(6),
        Validators.max(48)
      ]);
    } else if (type === 'CONSIGNED') {
      amountControl?.setValidators([
        Validators.required,
        Validators.min(2000),
        Validators.max(100000)
      ]);
      installmentsControl?.setValidators([
        Validators.required,
        Validators.min(12),
        Validators.max(84)
      ]);
    }

    amountControl?.updateValueAndValidity({ emitEvent: false });
    installmentsControl?.updateValueAndValidity({ emitEvent: false });
  }

  getInterestRate(): number {
    const type = this.form.get('type')?.value;
    return type === 'PERSONAL' ? 2.5 : 1.5;
  }

  getMinAmount(): number {
    const type = this.form.get('type')?.value;
    return type === 'PERSONAL' ? 1000 : 2000;
  }

  getMaxAmount(): number {
    const type = this.form.get('type')?.value;
    return type === 'PERSONAL' ? 50000 : 100000;
  }

  getMinInstallments(): number {
    const type = this.form.get('type')?.value;
    return type === 'PERSONAL' ? 6 : 12;
  }

  getMaxInstallments(): number {
    const type = this.form.get('type')?.value;
    return type === 'PERSONAL' ? 48 : 84;
  }

  isInvalid(ctrl: AbstractControl | null): boolean {
    return !!ctrl && ctrl.invalid && (ctrl.dirty || ctrl.touched);
  }

  getErrorMessage(ctrl: AbstractControl | null): string {
    if (!ctrl || !ctrl.errors) return '';
    
    if (ctrl.errors['required']) return 'Este campo é obrigatório';
    if (ctrl.errors['min']) return `Valor mínimo: ${ctrl.errors['min'].min}`;
    if (ctrl.errors['max']) return `Valor máximo: ${ctrl.errors['max'].max}`;
    
    return 'Valor inválido';
  }

  simulateLoan() {
    if (this.form.invalid || !this.userId()) {
      this.form.markAllAsTouched();
      return;
    }

    this.simulating.set(true);
    this.error.set(null);

    const formValue = this.form.value;
    const request = {
      totalAmount: formValue.totalAmount!,
      interestRate: this.getInterestRate(),
      numberOfInstallments: formValue.numberOfInstallments!,
      type: formValue.type!,
      userId: this.userId()!
    };

    this.loanService.simulateLoan(request).pipe(
      catchError((err) => {
        this.error.set(err.error?.message || 'Erro ao simular empréstimo. Verifique os dados informados.');
        return of(null);
      })
    ).subscribe({
      next: (simulation) => {
        if (simulation) {
          this.simulation.set(simulation);
        }
        this.simulating.set(false);
      }
    });
  }

  requestLoan() {
    if (this.form.invalid || !this.userId() || !this.simulation()) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.error.set(null);

    const formValue = this.form.value;
    const request = {
      totalAmount: formValue.totalAmount!,
      interestRate: this.getInterestRate(),
      numberOfInstallments: formValue.numberOfInstallments!,
      type: formValue.type!,
      userId: this.userId()!
    };

    this.loanService.requestLoan(request).pipe(
      catchError((err) => {
        this.error.set(err.error?.message || 'Erro ao solicitar empréstimo. Tente novamente mais tarde.');
        return of(null);
      })
    ).subscribe({
      next: (response) => {
        if (response) {
          this.loanResult.set(response);
          this.simulation.set(null);
          this.form.reset({
            type: 'PERSONAL'
          });
        }
        this.loading.set(false);
      }
    });
  }

  getStatusLabel(status: string): string {
    const labels: { [key: string]: string } = {
      'EM_ANALISE': 'Em Análise',
      'APROVADO': 'Aprovado',
      'REPROVADO': 'Reprovado',
      'ATIVO': 'Ativo',
      'QUITADO': 'Quitado',
      'INADIMPLENTE': 'Inadimplente'
    };
    return labels[status] || status;
  }

  getStatusClass(status: string): string {
    const classes: { [key: string]: string } = {
      'EM_ANALISE': 'bg-yellow-100 text-yellow-800 border-yellow-300',
      'APROVADO': 'bg-green-100 text-green-800 border-green-300',
      'REPROVADO': 'bg-red-100 text-red-800 border-red-300',
      'ATIVO': 'bg-blue-100 text-blue-800 border-blue-300',
      'QUITADO': 'bg-gray-100 text-gray-800 border-gray-300',
      'INADIMPLENTE': 'bg-orange-100 text-orange-800 border-orange-300'
    };
    return classes[status] || 'bg-gray-100 text-gray-800 border-gray-300';
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  }
}

