import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { RouterModule } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';
import { AuthService } from '../../services/auth.service';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterModule, LucideAngularModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class Login {
  loginForm: FormGroup;
  isLoading = false;
  errorMessage = '';
  passwordVisible = false;

  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router) {
    this.loginForm = this.fb.group({
      cpf: ['', [Validators.required, Validators.pattern(/^\d{11}$/)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      rememberMe: [true]
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      this.isLoading = true;
      const { cpf, password, rememberMe } = this.loginForm.value as { cpf: string; password: string; rememberMe: boolean };
      this.errorMessage = '';
      const normalizedCpf = String(cpf || '').replace(/\D+/g, '').trim();
      const normalizedPassword = String(password || '').trim();
      this.auth.login({ login: normalizedCpf, password: normalizedPassword })
        .pipe(finalize(() => { this.isLoading = false; }))
        .subscribe({
          next: (response) => {
            // AuthService stored token in localStorage by default. Move to session if not rememberMe.
            if (!rememberMe) {
              const tokenFromLocal = localStorage.getItem('auth_token') || response?.token;
              if (tokenFromLocal) {
                sessionStorage.setItem('auth_token', tokenFromLocal);
              }
              localStorage.removeItem('auth_token');
            }
            this.router.navigateByUrl('/dashboard');
          },
          error: (err) => {
            const backendMsg = err?.error?.message || err?.error?.error || err?.statusText;
            this.errorMessage = backendMsg ? `Login inválido: ${backendMsg}` : 'Login inválido. Verifique seus dados e tente novamente.';
          }
        });
    } else {
      this.markFormGroupTouched();
    }
  }

  togglePasswordVisibility() {
    this.passwordVisible = !this.passwordVisible;
  }

  private markFormGroupTouched() {
    Object.keys(this.loginForm.controls).forEach(key => {
      const control = this.loginForm.get(key);
      control?.markAsTouched();
    });
  }

  get cpf() { return this.loginForm.get('cpf'); }
  get password() { return this.loginForm.get('password'); }
  get rememberMe() { return this.loginForm.get('rememberMe'); }
}
