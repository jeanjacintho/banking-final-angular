import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Layout } from '../../components/layout/layout';
import { LucideAngularModule } from 'lucide-angular';
import { LoanResponse, LoanService } from '../../services/loan.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-loans',
  standalone: true,
  imports: [CommonModule, RouterModule, Layout, LucideAngularModule],
  templateUrl: './loans.html',
  styleUrl: './loans.css'
})
export class LoansPage implements OnInit {
  private loanService = inject(LoanService);
  private authService = inject(AuthService);

  loading = signal(false);
  error = signal<string | null>(null);
  loans = signal<LoanResponse[]>([]);
  userId = signal<number | null>(null);

  ngOnInit() {
    this.loading.set(true);
    this.authService.getUserInfo().subscribe({
      next: (user) => {
        const id = user?.id ?? null;
        this.userId.set(id);
        if (id) {
          this.fetchLoans(id);
        } else {
          this.loading.set(false);
          this.error.set('Não foi possível identificar o usuário.');
        }
      },
      error: () => {
        this.loading.set(false);
        this.error.set('Erro ao carregar dados do usuário.');
      }
    });
  }

  fetchLoans(id: number) {
    this.loanService.getUserLoans(id).subscribe({
      next: (items) => {
        this.loans.set(items ?? []);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Erro ao carregar empréstimos.');
        this.loading.set(false);
      }
    });
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
  }

  getStatusLabel(status: string): string {
    const labels: Record<string, string> = {
      EM_ANALISE: 'Em Análise',
      APROVADO: 'Aprovado',
      REPROVADO: 'Reprovado',
      ATIVO: 'Ativo',
      QUITADO: 'Quitado',
      INADIMPLENTE: 'Inadimplente'
    };
    return labels[status] ?? status;
  }

  getStatusClass(status: string): string {
    const classes: Record<string, string> = {
      EM_ANALISE: 'bg-yellow-50 text-yellow-800 border-yellow-200',
      APROVADO: 'bg-green-50 text-green-800 border-green-200',
      REPROVADO: 'bg-red-50 text-red-800 border-red-200',
      ATIVO: 'bg-blue-50 text-blue-800 border-blue-200',
      QUITADO: 'bg-gray-50 text-gray-800 border-gray-200',
      INADIMPLENTE: 'bg-orange-50 text-orange-800 border-orange-200'
    };
    return classes[status] ?? 'bg-gray-50 text-gray-800 border-gray-200';
  }
}
