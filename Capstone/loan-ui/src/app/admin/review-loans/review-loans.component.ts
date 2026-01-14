/*import { Component, OnInit } from '@angular/core';
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

 fetchLoans(): void {
  console.log('TOKEN:', localStorage.getItem('token'));

  this.loanService.getLoans().subscribe({
    next: (res: any) => {
      console.log('SUCCESS RESPONSE:', res);
      this.loans = res.content || [];
      this.loading = false;
    },
    error: (err: any) => {
      console.error('ANGULAR ERROR:', err);
      this.error = err.message || 'Failed to load loans';
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
*/



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

  constructor(private loanService: AdminLoanService) {}

  ngOnInit(): void {
    this.fetchLoans();
  }

  fetchLoans(): void {
    this.loanService.getLoans().subscribe((res: any) => {
      this.loans = res.content;   // ✅ paged response
      this.loading = false;
    });
  }

  moveToReview(id: string): void {
    this.loanService
      .changeStatus(id, 'UNDER_REVIEW', 'Moved to review')
      .subscribe(() => this.fetchLoans());
  }

  approve(id: string): void {
    this.loanService
      .changeStatus(id, 'APPROVED', 'Approved by admin')
      .subscribe(() => this.fetchLoans());
  }

  reject(id: string): void {
    this.loanService
      .changeStatus(id, 'REJECTED', 'Rejected by admin')
      .subscribe(() => this.fetchLoans());
  }
}