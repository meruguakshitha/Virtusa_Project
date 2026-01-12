import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { LoanService } from '../../loans/loan.service';

@Component({
  standalone: true,
  selector: 'app-apply-loan',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './apply-loan.component.html',
  styleUrls: ['./apply-loan.component.css']
})
export class ApplyLoanComponent implements OnInit {

  loanForm!: FormGroup;   // ✅ declare only
  emi = 0;
  submitting = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private loanService: LoanService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // ✅ initialize INSIDE ngOnInit
    this.loanForm = this.fb.group({
      clientName: ['', Validators.required],
      loanType: ['', Validators.required],
      requestedAmount: [null, Validators.required],
      proposedInterestRate: [null, Validators.required],
      tenureMonths: [null, Validators.required],
      financials: this.fb.group({
        revenue: [null, Validators.required],
        ebitda: [null, Validators.required],
        rating: ['', Validators.required]
      })
    });

    // auto EMI calculation
    this.loanForm.valueChanges.subscribe(() => {
      this.calculateEMI();
    });
  }

  calculateEMI() {
    const amount = this.loanForm.get('requestedAmount')?.value;
    const rate = this.loanForm.get('proposedInterestRate')?.value;
    const tenure = this.loanForm.get('tenureMonths')?.value;

    if (!amount || !rate || !tenure) {
      this.emi = 0;
      return;
    }

    const monthlyRate = rate / 12 / 100;
    const emi =
      (amount * monthlyRate * Math.pow(1 + monthlyRate, tenure)) /
      (Math.pow(1 + monthlyRate, tenure) - 1);

    this.emi = Math.round(emi);
  }

  submit() {
    if (this.loanForm.invalid) {
      this.errorMessage = 'All fields are required';
      return;
    }

    this.submitting = true;
    this.errorMessage = '';

    const payload = {
      ...this.loanForm.value,
      emi: this.emi
    };

    this.loanService.create(payload).subscribe({
      next: () => {
        alert('Loan submitted successfully');
        this.router.navigate(['/user/dashboard/my-loans']);
      },
      error: () => {
        this.errorMessage = 'Failed to submit loan';
        this.submitting = false;
      }
    });
  }
}
