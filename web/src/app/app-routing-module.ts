import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
//import { Login } from './pages/login/login';
import { Home } from './pages/home/home';

const routes: Routes = [
  { path: '', component: Home },
 // { path: 'login', component: Login }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
