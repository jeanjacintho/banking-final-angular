import { Component, Input, inject } from '@angular/core';
import { Router, RouterModule, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { LucideAngularModule } from 'lucide-angular';
import { AuthService } from '../../services/auth.service';
import { AccountBalanceComponent } from '../account-balance/account-balance';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-layout',
  imports: [CommonModule, LucideAngularModule, RouterModule, AccountBalanceComponent],
  templateUrl: './layout.html',
  styleUrls: ['./layout.css']
})
export class Layout {
  @Input() pageTitle: string = 'Dashboard';
  
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);
  
  userInfo: any = null;
  currentRoute: string = '';

  ngOnInit() {
    this.loadUserInfo();
    this.setupRouteTracking();
  }

  private setupRouteTracking() {
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.currentRoute = event.url;
      });
    
    // Set initial route
    this.currentRoute = this.router.url;
  }

  isActiveRoute(route: string): boolean {
    return this.currentRoute === route;
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
      error: (error) => {
        console.error('Layout - Error loading user info:', error);
        this.authService.clearToken();
        this.router.navigate(['/login']);
      }
    });
  }

  logout() {
    this.authService.clearToken();
    this.router.navigate(['/login']);
  }
}
