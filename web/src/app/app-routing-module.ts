import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CreditCardRequestComponent } from './Pages/credit-card/credit-card-request/credit-card-request';
import { CreditCardDashboardComponent } from './Pages/credit-card/credit-card-dashboard/credit-card-dashboard';

export const routes: Routes = [
  {path: 'credit-cards', children: [
    {path: '', component: CreditCardDashboardComponent},
    {path: 'request', component: CreditCardRequestComponent},
  ]}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
