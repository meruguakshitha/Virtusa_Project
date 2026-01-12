import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminLoanService } from '../services/admin-loan.service';

@Component({
  standalone: true,
  selector: 'app-review-loans',
  imports: [CommonModule],
  templateUrl: './review-loans.component.html',
  styleUrls: ['./review-loans.component.css']
})
export class ReviewLoansComponent implements OnInit {

  loans: any[] = [];
  loading = true;
  error = '';

  constructor(private loanService: AdminLoanService) {}

  ngOnInit(): void {
    this.fetchLoans();
  }

  // ✅ LOAD LOANS
  fetchLoans(): void {
    this.loanService.getLoans().subscribe({
      next: (res: any[]) => {
        this.loans = res;
        this.loading = false;
      },
      error: (err: any) => {
        console.error(err);
        this.error = 'Failed to load loans';
        this.loading = false;
      }
    });
  }

  // ✅ APPROVE
  approve(id: string): void {
    this.loanService.approveLoan(id).subscribe({
      next: () => this.fetchLoans(),
      error: (err: any) => {
        console.error(err);
        alert('Approve failed');
      }
    });
  }

  // ✅ REJECT
  reject(id: string): void {
    this.loanService.rejectLoan(id).subscribe({
      next: () => this.fetchLoans(),
      error: (err: any) => {
        console.error(err);
        alert('Reject failed');
      }
    });
  }
}
