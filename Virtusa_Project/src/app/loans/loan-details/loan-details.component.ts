import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';

import { LoanService } from '../loan.service';

@Component({
  selector: 'app-loan-details',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  templateUrl: './loan-details.component.html'
})
export class LoanDetailsComponent implements OnInit {

  loan: any;

  constructor(
    private route: ActivatedRoute,
    private loanService: LoanService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.loanService.getById(id).subscribe((res: any) => {
      this.loan = res;
    });
  }
}
