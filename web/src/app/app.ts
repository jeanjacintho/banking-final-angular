import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CreditCardDashboardComponent } from "./Pages/credit-card/credit-card-dashboard/credit-card-dashboard";

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  standalone: true,
  imports: [RouterOutlet, CreditCardDashboardComponent],
  styleUrl: './app.css'
})

export class AppComponent {
  demoCard = {
    id: 1,
    cardNumber: '4111111111111111',
    expirationDate: '12/2028',
    brand: 'VISA',
    holderName: 'JOÃO SILVA'
  };
}
