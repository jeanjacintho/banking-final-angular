import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Login } from './Pages/login/login';
import { Home } from './Pages/home/home';
import { Dashboard } from './Pages/dashboard/dashboard';
import { TransfersPage } from './Pages/transfers-page/transfers-page';
import { TransfersPageComponent } from './Pages/transfers/transfers';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'login', component: Login },
  { path: 'dashboard', component: Dashboard },
  { path: 'transfers', component: TransfersPage },
  {
    path: 'credit-card',
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./Pages/credit-card/credit-card-dashboard/credit-card-dashboard').then(
            (m) => m.CreditCardDashboardComponent
          ),
      },
      {
        path: 'request',
        loadComponent: () =>
          import('./components/credit-card/credit-card-request/credit-card-request').then(
            (m) => m.CreditCardRequestComponent
          ),
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
