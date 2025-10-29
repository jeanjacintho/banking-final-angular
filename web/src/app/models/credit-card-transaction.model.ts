export interface CreditCardTransaction {
  id: number;
  creditCardId: number;
  amount: number;
  currency: string;
  merchantName: string;
  mcc?: string;
  category?: string;
  status: 'AUTHORIZED' | 'POSTED' | 'REFUNDED';
  installmentsTotal?: number;
  createdAt: string;
  postedAt?: string;
}

export type CreateCreditCardTransactionRequest = {
  amount: number;
  merchantName: string;
  mcc?: string;
  category?: string;
  installmentsTotal?: number;
};



