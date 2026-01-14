/*export interface Loan {
  id: number;
  clientName: string;
  email: string;
  amount: number;
  tenure: string;
  type: string;
  emi: number;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
}*/


export type LoanStatus =
  | 'DRAFT'
  | 'SUBMITTED'
  | 'UNDER_REVIEW'
  | 'APPROVED'
  | 'REJECTED';

export interface Loan {
  id: string;                     // MongoDB ID
  clientName: string;
  loanType: string;
  requestedAmount: number;
  proposedInterestRate: number;
  tenureMonths: number;
  status: LoanStatus;
}