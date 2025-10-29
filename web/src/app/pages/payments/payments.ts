import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Layout } from '../../components/layout/layout';
import { TransferHistoryComponent } from '../../components/transfer-history/transfer-history';

@Component({
  selector: 'app-payments-page',
  standalone: true,
  imports: [CommonModule, RouterModule, Layout, TransferHistoryComponent],
  templateUrl: './payments.html',
  styleUrl: './payments.css'
})
export class PaymentsPage {}
