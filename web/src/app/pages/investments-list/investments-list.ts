import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Layout } from '../../components/layout/layout';
import { LucideAngularModule } from 'lucide-angular';
import { InvestmentResponse, InvestmentService } from '../../services/investment.service';

@Component({
  selector: 'app-investments-list',
  standalone: true,
  imports: [CommonModule, RouterModule, Layout, LucideAngularModule],
  templateUrl: './investments-list.html',
  styleUrl: './investments-list.css'
})
export class InvestmentsListPage implements OnInit {
  private investmentService = inject(InvestmentService);

  loading = signal(false);
  error = signal<string | null>(null);
  items = signal<InvestmentResponse[]>([]);

  ngOnInit() {
    this.loading.set(true);
    this.investmentService.getMyInvestments().subscribe({
      next: (list) => {
        this.items.set(list || []);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Erro ao carregar investimentos.');
        this.loading.set(false);
      }
    });
  }

  formatCurrency(value?: number): string {
    if (value == null) return '-';
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
  }
}
