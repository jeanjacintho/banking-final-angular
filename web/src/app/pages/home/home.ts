import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { RegisterComponent } from '../../components/register-component/register-component';
import { CurrencyConverterComponent } from "../../components/currency-converter/currency-converter";

@Component({
  selector: 'app-home',
  standalone: true,
  templateUrl: './home.html',
  imports: [
    CommonModule,
    RegisterComponent
],
    CurrencyConverterComponent
  ],
  styleUrls: ['./home.css']
})
export class Home {

  menuOpen = false;
  showForm = false;
  isVisible = false

  constructor(private router: Router) {}

  navigateToLogin() {
    this.router.navigate(['/login']);
  }

  toggleMenu() {
    this.menuOpen = !this.menuOpen;
  }

  openForm(){
    this.isVisible = true;
    setTimeout(() => this.showForm = true, 10)
  }

  closeForm(){
    this.showForm = false;
    setTimeout(() => this.isVisible = false, 300)
  }

}
