import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LucideAngularModule } from 'lucide-angular';
import { PixService, PixKey, CreatePixKeyRequest } from '../../services/pix.service';
import { AccountService, BankAccount } from '../../services/account.service';

@Component({
  selector: 'app-pix-keys',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, LucideAngularModule],
  templateUrl: './pix-keys.html',
  styleUrls: ['./pix-keys.css']
})
export class PixKeysComponent implements OnInit {
  private readonly pixService = inject(PixService);
  private readonly accountService = inject(AccountService);
  private readonly fb = inject(FormBuilder);

  pixForm: FormGroup;
  userAccounts: BankAccount[] = [];
  pixKeys: PixKey[] = [];
  selectedAccount: BankAccount | null = null;
  selectedAccountId: number | null = null;
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  showForm = false;
  showSuccessModal = false;

  keyTypes = [
    { value: 'CPF', label: 'CPF', placeholder: '000.000.000-00' },
    { value: 'EMAIL', label: 'E-mail', placeholder: 'seu@email.com' },
    { value: 'PHONE', label: 'Telefone', placeholder: '+5511999999999' }
  ];

  constructor() {
    this.pixForm = this.fb.group({
      accountId: ['', Validators.required],
      keyType: ['', Validators.required],
      keyValue: ['', [Validators.required, Validators.minLength(1)]]
    });
  }

  ngOnInit() {
    this.loadUserAccounts();
  }

  loadUserAccounts() {
    this.accountService.getUserAccounts().subscribe({
      next: (accounts) => {
        this.userAccounts = accounts;
        if (accounts.length > 0) {
          this.selectedAccount = accounts[0];
          this.selectedAccountId = accounts[0].id;
          this.pixForm.patchValue({ accountId: accounts[0].id });
          this.loadPixKeys(accounts[0].id);
        }
      },
      error: (error) => {
        console.error('Erro ao carregar contas:', error);
        // Se o backend retornar 404 ou lista vazia, não mostrar erro
        if (error?.status === 404) {
          this.userAccounts = [];
          this.errorMessage = '';
        } else {
          this.errorMessage = 'Erro ao carregar suas contas';
        }
      }
    });
  }

  onAccountChange(event: Event) {
    const target = event.target as HTMLSelectElement;
    const accountId = +target.value;
    this.selectedAccountId = accountId;
    this.selectedAccount = this.userAccounts.find(acc => acc.id === accountId) || null;
    if (accountId) {
      this.loadPixKeys(accountId);
    }
    this.clearMessages();
  }

  selectAccount(account: BankAccount) {
    this.selectedAccount = account;
    this.selectedAccountId = account.id;
    this.pixForm.patchValue({ accountId: account.id });
    this.loadPixKeys(account.id);
    this.clearMessages();
  }

  loadPixKeys(accountId: number) {
    this.isLoading = true;
    this.pixService.getPixKeysByAccount(accountId).subscribe({
      next: (response) => {
        if (response.success) {
          this.pixKeys = response.keys;
        } else {
          this.errorMessage = response.message || 'Erro ao carregar chaves PIX';
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Erro ao carregar chaves PIX:', error);
        this.errorMessage = 'Erro ao carregar chaves PIX';
        this.isLoading = false;
      }
    });
  }

  onKeyTypeChange() {
    const keyType = this.pixForm.get('keyType')?.value;
    const keyValueControl = this.pixForm.get('keyValue');
    
    keyValueControl?.setValue('');
    keyValueControl?.clearValidators();
    
    if (keyType === 'CPF') {
      keyValueControl?.setValidators([
        Validators.required,
        Validators.pattern(/^\d{3}\.\d{3}\.\d{3}-\d{2}$|^\d{11}$/)
      ]);
    } else if (keyType === 'EMAIL') {
      keyValueControl?.setValidators([
        Validators.required,
        Validators.email
      ]);
    } else if (keyType === 'PHONE') {
      keyValueControl?.setValidators([
        Validators.required,
        Validators.pattern(/^\+?[1-9]\d{1,14}$/)
      ]);
    }
    
    keyValueControl?.updateValueAndValidity();
  }

  formatKeyValue(event: any) {
    const keyType = this.pixForm.get('keyType')?.value;
    let value = event.target.value;
    
    if (keyType === 'CPF') {
      // Remove tudo que não é dígito
      value = value.replace(/\D/g, '');
      if (value.length <= 11) {
        value = value.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
        value = value.replace(/(\d{3})(\d{3})(\d{3})/, '$1.$2.$3');
        value = value.replace(/(\d{3})(\d{3})/, '$1.$2');
      }
    } else if (keyType === 'PHONE') {
      // Remove tudo que não é dígito ou +
      value = value.replace(/[^\d+]/g, '');
      if (value.length > 0 && !value.startsWith('+')) {
        value = '+55' + value;
      }
    } else if (keyType === 'EMAIL') {
      // Para e-mail, não fazemos formatação automática
      // Apenas mantemos o valor original
      value = event.target.value;
    }
    
    this.pixForm.patchValue({ keyValue: value });
  }

  onSubmit() {
    if (this.pixForm.valid) {
      this.isLoading = true;
      this.clearMessages();
      
      const formValue = this.pixForm.value;
      const request: CreatePixKeyRequest = {
        accountId: formValue.accountId,
        keyType: formValue.keyType,
        keyValue: formValue.keyValue
      };

      this.pixService.createPixKey(request).subscribe({
        next: (response) => {
          if (response.success) {
            this.successMessage = response.message;
            this.showSuccessModal = true;
            this.pixForm.reset();
            this.pixForm.patchValue({ accountId: this.selectedAccount?.id });
            this.loadPixKeys(this.selectedAccount!.id);
            this.showForm = false;
          } else {
            this.errorMessage = response.message;
          }
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Erro ao criar chave PIX:', error);
          this.errorMessage = 'Erro ao criar chave PIX. Tente novamente.';
          this.isLoading = false;
        }
      });
    }
  }

  deletePixKey(keyId: number) {
    if (confirm('Tem certeza que deseja remover esta chave PIX?')) {
      this.isLoading = true;
      this.pixService.deletePixKey(keyId).subscribe({
        next: (response) => {
          if (response.success) {
            this.successMessage = response.message;
            this.loadPixKeys(this.selectedAccount!.id);
          } else {
            this.errorMessage = response.message;
          }
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Erro ao deletar chave PIX:', error);
          this.errorMessage = 'Erro ao remover chave PIX';
          this.isLoading = false;
        }
      });
    }
  }

  toggleForm() {
    this.showForm = !this.showForm;
    this.clearMessages();
    if (this.showForm) {
      this.pixForm.patchValue({ 
        accountId: this.selectedAccount?.id,
        keyType: '',
        keyValue: ''
      });
    }
  }

  clearMessages() {
    this.errorMessage = '';
    this.successMessage = '';
  }

  getKeyTypeLabel(keyType: string): string {
    const type = this.keyTypes.find(t => t.value === keyType);
    return type ? type.label : keyType;
  }

  formatKeyValueDisplay(keyValue: string, keyType: string): string {
    if (!keyValue) {
      return 'Valor não disponível';
    }
    
    if (keyType === 'CPF') {
      // Remove formatação existente e aplica nova
      const cleanValue = keyValue.replace(/\D/g, '');
      if (cleanValue.length === 11) {
        return cleanValue.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
      }
      return cleanValue;
    }
    
    if (keyType === 'PHONE') {
      // Garantir que o telefone tenha formatação adequada
      const cleanValue = keyValue.replace(/\D/g, '');
      if (cleanValue.length >= 10) {
        return keyValue; // Manter formatação original se já estiver formatado
      }
      return cleanValue;
    }
    
    return keyValue;
  }

  getPlaceholderForKeyType(): string {
    const keyType = this.pixForm.get('keyType')?.value;
    const type = this.keyTypes.find(t => t.value === keyType);
    return type ? type.placeholder : 'Digite o valor';
  }

  selectKeyType(keyType: string) {
    this.pixForm.patchValue({ keyType });
    this.onKeyTypeChange();
  }

  getKeyTypeDescription(keyType: string): string {
    switch (keyType) {
      case 'CPF':
        return 'Seu CPF pessoal';
      case 'EMAIL':
        return 'Seu e-mail';
      case 'PHONE':
        return 'Seu telefone';
      default:
        return '';
    }
  }

  getAccountTypeLabel(accountType: string): string {
    return accountType === 'CHECKING' ? 'Conta Corrente' : 'Conta Poupança';
  }

  formatBalance(balance: number): string {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(balance);
  }

  closeSuccessModal() {
    this.showSuccessModal = false;
    this.successMessage = '';
  }

  copyToClipboard(key: PixKey) {
    const textToCopy = this.formatKeyValueDisplay(key.keyValue, key.keyType);
    navigator.clipboard.writeText(textToCopy).then(() => {
      this.successMessage = `Chave ${this.getKeyTypeLabel(key.keyType)} copiada!`;
      setTimeout(() => this.clearMessages(), 3000);
    }).catch(() => {
      this.errorMessage = 'Erro ao copiar chave';
      setTimeout(() => this.clearMessages(), 3000);
    });
  }

  viewAllKeys() {
    // Scroll to keys section or expand view
    const keysSection = document.querySelector('.keys-section');
    if (keysSection) {
      keysSection.scrollIntoView({ behavior: 'smooth' });
    }
  }

  getTypeIcon(keyType: string): string {
    switch (keyType) {
      case 'CPF':
        return '👤';
      case 'EMAIL':
        return '📧';
      case 'PHONE':
        return '📱';
      default:
        return '🔑';
    }
  }

  getTypeDescription(keyType: string): string {
    switch (keyType) {
      case 'CPF':
        return 'Seu CPF pessoal';
      case 'EMAIL':
        return 'Seu endereço de e-mail';
      case 'PHONE':
        return 'Seu número de telefone';
      default:
        return '';
    }
  }

  shareKey(key: PixKey) {
    const textToShare = `Minha chave PIX ${this.getKeyTypeLabel(key.keyType)}: ${this.formatKeyValueDisplay(key.keyValue, key.keyType)}`;
    
    if (navigator.share) {
      navigator.share({
        title: 'Chave PIX',
        text: textToShare
      }).then(() => {
        this.successMessage = 'Chave compartilhada com sucesso!';
        setTimeout(() => this.clearMessages(), 3000);
      }).catch(() => {
        this.copyToClipboard(key);
      });
    } else {
      this.copyToClipboard(key);
    }
  }

  clearForm() {
    this.pixForm.patchValue({
      keyType: '',
      keyValue: ''
    });
    this.clearMessages();
  }
}