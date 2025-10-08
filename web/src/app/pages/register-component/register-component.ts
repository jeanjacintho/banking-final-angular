import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register-component',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './register-component.html',
  styleUrl: './register-component.css'
})
export class RegisterComponent {
  registerForm: FormGroup;
  isSubmiting = false;
  
  constructor(private fb: FormBuilder, private router: Router) {
    this.registerForm = this.fb.group({
      nomeCompleto:   ['', [Validators.required, Validators.minLength(3), Validators.maxLength(120)]],
      telefone:       ['', [Validators.required, Validators.pattern(/^\d{10,11}$/)]],
      email:          ['', [Validators.required, Validators.email]],
      cpf:            ['', [Validators.required, Validators.pattern(/^\d{11}$/)]],
      dataNascimento: ['', [Validators.required]],
      consentimento:  [false, Validators.requiredTrue]
    });

    //Log para debug
    console.log('Form criado', this.registerForm);
  }

  onSubmit(){
    console.log("submit chamado!")
    this.markFormGroupTouched();
    if (this.registerForm.invalid) {
      console.log("Form inválido - marcado como touched!")
      this.markFormGroupTouched();
      return;      
    }

    console.log("Form válido!")
    this.isSubmiting = true;
  }

  private markFormGroupTouched() {
    Object.keys(this.registerForm.controls).forEach(key => {
      const control = this.registerForm.get(key);
      control?.markAsTouched();
    });
  }


  navigateToLogin(){
    this.router.navigate(['/login'])
  }

  get nomeCompleto() { return this.registerForm.get('nomeCompleto'); }
  get telefone() { return this.registerForm.get('telefone');} 
  get email() { return this.registerForm.get('email'); }
  get cpf() { return this.registerForm.get('cpf'); }
  get dataNascimento() { return this.registerForm.get('dataNascimento'); }
  get consentimento() { return this.registerForm.get('consentimento'); }
  
}
