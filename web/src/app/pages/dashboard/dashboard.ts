import { Component, OnInit, inject } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { BankAccountsComponent } from '../../components/bank-accounts/bank-accounts';
import { TransfersComponent } from '../../components/transfers/transfers';
import { TransferHistoryComponent } from '../../components/transfer-history/transfer-history';
import { CreditCardComponent } from '../../components/credit-card/credit-card';
import { Layout } from '../../components/layout/layout';
import { BankAccount } from '../../services/account.service';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    BankAccountsComponent, 
    TransfersComponent, 
    TransferHistoryComponent, 
    CreditCardComponent, 
    RouterModule, 
    LucideAngularModule,
    Layout
    
  ],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css']
})
export class Dashboard implements OnInit {
  private readonly router = inject(Router);

  ngOnInit() {
    // Dashboard initialization logic can go here
  }

  onAccountClick(account: BankAccount) {
    console.log('Account clicked:', account);
    // TODO: Navigate to account details or perform action
  }

  onCreateAccount(accountType: string) {
    console.log('Create account:', accountType);
    // TODO: Implement account creation logic
  }

}
