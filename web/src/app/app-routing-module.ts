import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { Home } from './pages/home/home';
import { Dashboard } from './pages/dashboard/dashboard';
import { TransfersPage } from './pages/transfers-page/transfers-page';
import { CreditCardRequestComponent } from './components/credit-card/credit-card-request/credit-card-request';
import { CreditCardDashboardComponent } from './pages/credit-card/credit-card-dashboard/credit-card-dashboard';
import { LoanRequestComponent } from './pages/loan-request/loan-request';
import { LoansPage } from './pages/loans/loans';
import { InvestmentsPage } from './pages/investments-page/investments-page';
import { InvestmentsListPage } from './pages/investments-list/investments-list';
import { PaymentsPage } from './pages/payments/payments';
import { CurrencyPage } from './pages/currency-page/currency-page';
import { PixKeysPage } from './pages/pix-keys-page/pix-keys-page';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'dashboard', component: Dashboard },
  { path: 'transfers', component: TransfersPage },
  {
    path: 'credit-card',
    children: [
      { path: '', component: CreditCardDashboardComponent },
      { path: 'request', component: CreditCardRequestComponent },
    ],
  },
  { path: 'payments', component: PaymentsPage },
  { path: 'currency', component: CurrencyPage },
  { path: 'investiments', component: InvestmentsListPage },
  { path: 'investiment/request', component: InvestmentsPage },
  { path: 'pix-keys', component: PixKeysPage },
  { path: 'loans', component: LoansPage },
  { path: 'loans/request', component: LoanRequestComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
