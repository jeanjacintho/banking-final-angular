import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Home } from './pages/home/home';
import { Dashboard } from './pages/dashboard/dashboard';
import { TransfersPage } from './pages/transfers-page/transfers-page';
import { CreditCardRequestComponent } from './components/credit-card/credit-card-request/credit-card-request';
import { CreditCardDashboardComponent } from './pages/credit-card/credit-card-dashboard/credit-card-dashboard';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'login', component: Login },
  { path: 'dashboard', component: Dashboard },
  { path: 'transfers', component: TransfersPage },
  {
    path: 'credit-card',
    children: [
      { path: '', component: CreditCardDashboardComponent },
      { path: 'request', component: CreditCardRequestComponent },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
