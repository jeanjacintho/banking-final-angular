export interface CreditCard {
    id: number;
    cardNumber: string;
    cardHolderName: string;
    expMonth: number;
    expYear: number;
    cvvHash: string;
    cvv?: string; // CVV descriptografado (apenas quando recuperado)
    maskedPan: string;
    panToken: string;
    brand: string;
    creditLimit: number;
    availableLimit: number;
    isActive: boolean;
    createdAt?: string;
    updatedAt?: string;
}