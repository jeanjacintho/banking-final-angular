import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { Layout } from '../../components/layout/layout';
import { LucideAngularModule } from 'lucide-angular';
import { AccountService, BankAccount } from '../../services/account.service';
import { InvestmentService } from '../../services/investment.service';

@Component({
  selector: 'app-investments-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, Layout, LucideAngularModule],
  templateUrl: './investments-page.html',
  styleUrl: './investments-page.css'
})
export class InvestmentsPage implements OnInit {
  private fb = inject(FormBuilder);
  private accountService = inject(AccountService);
  private investmentService = inject(InvestmentService);

  loading = signal(false);
  accounts = signal<BankAccount[]>([]);
  error = signal<string | null>(null);
  success = signal<string | null>(null);

  form = this.fb.group({
    accountNumber: ['', Validators.required],
    type: ['CDB', Validators.required], // 'CDB' | 'RENDA_FIXA'
    value: [null as number | null, [Validators.required, Validators.min(1)]],
    term: [null as number | null] // somente para CDB (em meses)
  });

  ngOnInit() {
    this.loading.set(true);
    this.accountService.getUserAccounts().subscribe({
      next: (accs) => {
        this.accounts.set(accs || []);
        if (accs && accs.length) this.form.patchValue({ accountNumber: accs[0].accountNumber });
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Erro ao carregar contas.');
        this.loading.set(false);
      }
    });

    this.form.get('type')?.valueChanges.subscribe((t) => {
      if (t === 'CDB') {
        this.form.get('term')?.setValidators([Validators.required, Validators.min(1)]);
      } else {
        this.form.get('term')?.clearValidators();
      }
      this.form.get('term')?.updateValueAndValidity({ emitEvent: false });
    });
  }

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.error.set(null);
    this.success.set(null);

    const { accountNumber, type, value, term } = this.form.value;
    if (type === 'CDB') {
      this.investmentService.createCdb({ accountNumber: accountNumber!, term: term!, value: value! }).subscribe({
        next: () => {
          this.success.set('CDB criado com sucesso.');
        },
        error: (e) => {
          this.error.set(e?.error?.error || 'Erro ao criar CDB.');
        }
      });
    } else {
      this.investmentService.createRendaFixa({ accountNumber: accountNumber!, value: value! }).subscribe({
        next: () => {
          this.success.set('Renda Fixa criada com sucesso.');
        },
        error: (e) => {
          this.error.set(e?.error?.error || 'Erro ao criar Renda Fixa.');
        }
      });
    }
  }
}
