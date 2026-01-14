import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoanService } from '../../loans/loan.service';
import { finalize } from 'rxjs/operators';

@Component({
  standalone: true,
  selector: 'app-my-loans',
  imports: [CommonModule],
  templateUrl: './my-loans.component.html',
  styleUrls: ['./my-loans.component.css']
})
export class MyLoansComponent implements OnInit {

  loans: any[] = [];
  loading = true;

  constructor(
    private loanService: LoanService,
    private cdr: ChangeDetectorRef   // ✅ ADD
  ) {}

  ngOnInit(): void {
    console.log('MY LOANS INIT');

    this.loanService.getMyLoans()
      .pipe(
        finalize(() => {
          this.loading = false;     // ✅ ALWAYS STOP LOADING
          this.cdr.detectChanges(); // ✅ FORCE UI UPDATE
          console.log('FINALIZE FIRED, loading=false');
        })
      )
      .subscribe({
        next: (res: any) => {
          console.log('MY LOANS RESPONSE:', res);

          const data = Array.isArray(res) ? res : res?.content || [];

          this.loans = data.map((loan: any) => ({
            ...loan,
            emi: this.calculateEmi(
              loan.requestedAmount,
              loan.proposedInterestRate,
              loan.tenureMonths
            )
          }));
        },
        error: (err) => {
          console.error('MY LOANS ERROR:', err);
        }
      });
  }

  calculateEmi(amount: number, rate: number, tenure: number): number {
    const monthlyRate = rate / 12 / 100;
    const emi =
      (amount * monthlyRate * Math.pow(1 + monthlyRate, tenure)) /
      (Math.pow(1 + monthlyRate, tenure) - 1);

    return Math.round(emi);
  }
}
