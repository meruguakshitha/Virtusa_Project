import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-loan-edit',
  
  templateUrl: './loan-edit.component.html',
  imports: [CommonModule, ReactiveFormsModule]
})
export class LoanEditComponent {

  loanForm!: FormGroup;   // ✅ declare only

  constructor(private fb: FormBuilder) {
    // ✅ initialize AFTER fb is available
    this.loanForm = this.fb.group({
      amount: [''],
      tenure: [''],
      interestRate: ['']
    });
  }

  updateLoan() {
    console.log(this.loanForm.value);
  }
}
