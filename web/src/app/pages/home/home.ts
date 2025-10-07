import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  templateUrl: './home.html',
  imports: [
    CommonModule
  ],
  styleUrls: ['./home.css']
})
export class Home {
  menuOpen = false;

  constructor(private router: Router) {}

  navigateToLogin() {
    this.router.navigate(['/login']);
  }

  toggleMenu() {
    this.menuOpen = !this.menuOpen;
  }
}
