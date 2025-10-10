import { Component, OnInit, inject } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { BankAccountsComponent } from '../../components/bank-accounts/bank-accounts';
import { TransfersComponent } from '../../components/transfers/transfers';
import { TransferHistoryComponent } from '../../components/transfer-history/transfer-history';
import { BankAccount } from '../../services/account.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [BankAccountsComponent, TransfersComponent, TransferHistoryComponent, RouterModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css']
})
export class Dashboard implements OnInit {
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);

  userInfo: any = null;

  ngOnInit() {
    this.loadUserInfo();
  }

  private loadUserInfo() {
    const token = this.authService.getToken();
    if (!token) {
      this.router.navigate(['/login']);
      return;
    }
    this.authService.getUserInfo().subscribe({
      next: (user) => {
        this.userInfo = {
          name: user?.nomeCompleto || user?.name || 'Usuário',
          cpf: user?.cpf || '',
          email: user?.email || ''
        };
      },
      error: () => {
        this.authService.clearToken();
        this.router.navigate(['/login']);
      }
    });
  }

  onAccountClick(account: BankAccount) {
    console.log('Account clicked:', account);
    // TODO: Navigate to account details or perform action
  }

  onCreateAccount(accountType: string) {
    console.log('Create account:', accountType);
    // TODO: Implement account creation logic
  }

  logout() {
    this.authService.clearToken();
    this.router.navigate(['/login']);
  }
}
