export interface Loan {
  id: number;
  clientName: string;
  email: string;
  amount: number;
  tenure: string;
  type: string;
  emi: number;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
}
