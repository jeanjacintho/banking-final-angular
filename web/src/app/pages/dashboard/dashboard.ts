import { Component, OnInit, inject, ViewChild, ElementRef } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { BankAccountsComponent } from '../../components/bank-accounts/bank-accounts';
import { TransferHistoryComponent } from '../../components/transfer-history/transfer-history';
import { CreditCardComponent } from '../../components/credit-card/credit-card';
import { CurrencyConverterComponent } from '../../components/currency-converter/currency-converter';
import { PixKeysComponent } from '../../components/pix-keys/pix-keys';
import { Layout } from '../../components/layout/layout';
import { BankAccount } from '../../services/account.service';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    BankAccountsComponent, 
    TransferHistoryComponent, 
    CreditCardComponent,
    CurrencyConverterComponent,
    PixKeysComponent,
    RouterModule, 
    LucideAngularModule,
    Layout
    
  ],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css']
})
export class Dashboard implements OnInit {
  private readonly router = inject(Router);
  
  @ViewChild('pixKeysSection', { static: false }) pixKeysSection!: ElementRef;

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

  scrollToPixKeys() {
    if (this.pixKeysSection) {
      this.pixKeysSection.nativeElement.scrollIntoView({ 
        behavior: 'smooth', 
        block: 'start' 
      });
    }
  }

}
