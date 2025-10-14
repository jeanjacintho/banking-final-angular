import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Home } from './pages/home/home';
import { Dashboard } from './pages/dashboard/dashboard';
import { TransfersPage } from './pages/transfers-page/transfers-page';
import { TransfersPageComponent } from './pages/transfers/transfers';
import { CreditCardsPageComponent } from './pages/credit-cards/credit-cards';

const routes: Routes = [
  { path: '', component: Home },
  { path: 'login', component: Login },
  { path: 'dashboard', component: Dashboard },
  { path: 'transfers', component: TransfersPage },
  { path: 'credit-cards', component: CreditCardsPageComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
