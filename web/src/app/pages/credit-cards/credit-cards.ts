import { Component } from '@angular/core';
import { CreditCardComponent } from '../../components/credit-card/credit-card';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-credit-cards-page',
  standalone: true,
  imports: [CommonModule, RouterModule, CreditCardComponent],
  template: `
    <div class="min-h-screen bg-gradient-to-b from-background to-gradient-start/40 text-text p-8">
      <div class="max-w-7xl mx-auto">
        <div class="mb-6">
          <button 
            routerLink="/dashboard"
            class="flex items-center gap-2 text-text-light hover:text-text transition-colors">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
            </svg>
            Voltar ao Dashboard
          </button>
        </div>
        
        <h1 class="text-3xl font-bold mb-6">Cartões de Crédito</h1>
        
        <app-credit-card></app-credit-card>
      </div>
    </div>
  `,
  styles: []
})
export class CreditCardsPageComponent { }



