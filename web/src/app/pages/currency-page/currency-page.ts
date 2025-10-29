import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Layout } from '../../components/layout/layout';
import { CurrencyConverterComponent } from '../../components/currency-converter/currency-converter';

@Component({
  selector: 'app-currency-page',
  standalone: true,
  imports: [CommonModule, RouterModule, Layout, CurrencyConverterComponent],
  templateUrl: './currency-page.html',
  styleUrl: './currency-page.css'
})
export class CurrencyPage {}
