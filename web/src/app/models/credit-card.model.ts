export interface CreditCard {
maskedPan: any;
    id: number;
    cardNumber: string;
    cardHolderName: string;
    expirationDate: string;
    cvv: string;
    brand: string;
    creditLimit: number;
    availableLimit: number;
    isActive: boolean;
    createdAt: string;
    updatedAt: string;
}