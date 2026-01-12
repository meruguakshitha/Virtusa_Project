export interface Loan {
  id?: string;
  clientName: string;
  loanType: string;
  requestedAmount: number;
  proposedInterestRate: number;
  tenureMonths: number;
  status: string;
  financials: {
    revenue: number;
    ebitda: number;
    rating: string;
  };
}
