import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Component } from '@angular/core';
import { environment } from '../../../environments/environment';
import { AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { NgxMaskDirective, provideNgxMask  } from 'ngx-mask';

@Component({
  selector: 'app-register-component',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    CommonModule,
    HttpClientModule,
    NgxMaskDirective,
  ],
  providers: [provideNgxMask()],
  templateUrl: './register-component.html',
  styleUrls: ['./register-component.css']
})
export class RegisterComponent {
  registerForm: FormGroup;
  isSubmiting = false;

  constructor(private fb: FormBuilder, private router: Router, private http: HttpClient) {
    this.registerForm = this.fb.group({
      nomeCompleto:   ['', [Validators.required, Validators.minLength(3), Validators.maxLength(120)]],
      telefone:       ['', [Validators.required, Validators.pattern(/^\d{10,11}$/)]],
      email:          ['', [Validators.required, Validators.email]],
      cpf:            ['', [Validators.required, Validators.pattern(/^\d{11}$/), Validators.minLength(11), Validators.maxLength(11), this.cpfValidator()]],
      dataNascimento: ['', [Validators.required, this.minimumAgeValidator(18)]],
      cep:            ['', [Validators.required, Validators.pattern(/^\d{8}$/)]],
      consentimento:  [false, Validators.requiredTrue],
      senha:          ['', [Validators.required, this.strongPasswordValidator()]],
      logradouro: [{ value: '', disabled: true }],
      bairro: [{ value: '', disabled: true }],
      localidade: [{ value: '', disabled: true }],
      uf: [{ value: '', disabled: true }],
    });

    //Log para debug
    console.log('Form criado', this.registerForm);
  }

  public onSubmit(): void {
    console.log("submit chamado!")
    this.markFormGroupTouched();
    if (this.registerForm.invalid) {
      console.log("Form inválido - marcado como touched!")
      this.markFormGroupTouched();
      return;      
    }

    console.log("Form válido!")
    this.isSubmiting = true;

    // Usar getRawValue() para incluir campos desabilitados
    const formValues = this.registerForm.getRawValue();
    const {logradouro, bairro, localidade, uf, cep, ...rest} = formValues;
    
    // Montar endereço completo com todos os campos
    const enderecoParts = [];
    if (logradouro) enderecoParts.push(logradouro);
    if (bairro) enderecoParts.push(bairro);
    if (localidade && uf) {
      enderecoParts.push(`${localidade} - ${uf}`);
    } else if (localidade) {
      enderecoParts.push(localidade);
    } else if (uf) {
      enderecoParts.push(uf);
    }
    if (cep) {
      const cepFormatado = cep.replace(/\D/g, '').replace(/(\d{5})(\d{3})/, '$1-$2');
      enderecoParts.push(`CEP: ${cepFormatado}`);
    }
    
    const enderecoCompleto = enderecoParts.join(', ');

    const payload = {
      ...rest,
      enderecoCompleto: enderecoCompleto,
      status: 'ATIVO',
      userRole: 'CLIENTE'
    };

    console.log("Payload para envio:", payload);

    this.http.post(`${environment.apiBase}/usuarios`, payload).subscribe({
      next: (response) => {
        console.log("Usuário criado com sucesso!", response);
        this.router.navigate(['/login']);
      },
      error: (error) => {
        console.error("Erro ao criar usuário", error);
        this.isSubmiting = false;
      }
    });
  }

  private markFormGroupTouched(formGroup: FormGroup = this.registerForm) {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);

      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      } else {
        control?.markAsTouched();
      }      
    });
  }


  public navigateToLogin(): void{
    this.router.navigate(['/login'])
  }

  public buscarCEP(): void {
    const cepValue = this.cep?.value?.replace(/\D/g, '');
    if (!cepValue || cepValue.length !== 8) {
      return;
    }
    
    this.http.get<any>(`https://viacep.com.br/ws/${cepValue}/json/`).subscribe({
      next: (data) => {
        if (data.erro) {
          console.error('CEP não encontrado');
          return;
        }
        // patchValue funciona mesmo com campos desabilitados
        this.registerForm.patchValue({
          logradouro: data.logradouro || '',
          bairro: data.bairro || '',
          localidade: data.localidade || '',
          uf: data.uf || ''
        }, { emitEvent: false });
      },
      error: (error) => {
        console.error('Erro ao buscar CEP', error);
      }
    });
  }

  public validaCPF(cpf: string): boolean {
    cpf = cpf.replace(/\D/g, '');
    if(cpf.length != 11 || /^(\d)\1+$/.test(cpf)) return false;
    let sum = 0, resto;

    for (let i = 1; i <= 9; i++) sum += parseInt(cpf.substring(i-1, i)) * (11 - i);
    resto = (sum * 10) % 11;
    if (resto == 10 || resto == 11) resto = 0;
    if (resto != parseInt(cpf.substring(9, 10))) return false;

    sum = 0;
    for (let i = 1; i <= 10; i++) sum += parseInt(cpf.substring(i-1, i)) * (12 - i);
    resto = (sum * 10) % 11;
    if (resto === 10 || resto === 11) resto = 0;

    return resto === parseInt(cpf.substring(10, 11));
  }

  private cpfValidator(): ValidatorFn{
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value.replace(/\D/g, '');
      if (!value) return null;
      return this.validaCPF(value) ? null : { invalidCPF: true };
    };
  }

  private strongPasswordValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (!value) return null;

      const strongPasswordRegex = /^(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).{8,}$/;
      return strongPasswordRegex.test(value) ? null : { weakPassword: true };
    };
  }

  private minimumAgeValidator(minAge: number): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value = control.value;
      if (!value) return null;

      const birthDate = new Date(value);
      const today = new Date();
      const age = today.getFullYear() - birthDate.getFullYear();
      const hasHadBirthdayPassed = 
        today.getMonth() > birthDate.getMonth() ||
        (today.getMonth() === birthDate.getMonth() && today.getDate() >= birthDate.getDate());

      const actualAge = hasHadBirthdayPassed ? age : age - 1;
      return actualAge >= minAge ? null : { underage: true };
    };
  }

  get nomeCompleto() { return this.registerForm.get('nomeCompleto'); }
  get telefone() { return this.registerForm.get('telefone'); }
  get email() { return this.registerForm.get('email'); }
  get cpf() { return this.registerForm.get('cpf'); }
  get dataNascimento() { return this.registerForm.get('dataNascimento'); }
  get cep() { return this.registerForm.get('cep'); }
  get consentimento() { return this.registerForm.get('consentimento'); }
  get logradouro() { return this.registerForm.get('logradouro'); }
  get bairro() { return this.registerForm.get('bairro'); }
  get localidade() { return this.registerForm.get('localidade'); }
  get uf() { return this.registerForm.get('uf'); }
  get senha() { return this.registerForm.get('senha'); }

}
