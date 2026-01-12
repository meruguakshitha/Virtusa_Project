import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatButtonModule } from '@angular/material/button';

import { LoanService } from '../loan.service';

@Component({
  selector: 'app-loan-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatTableModule,
    MatCardModule,
    MatChipsModule,
    MatButtonModule
  ],
  templateUrl: './loan-list.component.html',
  styleUrls: ['./loan-list.component.css']
})
export class LoanListComponent implements OnInit {

  loans: any[] = [];

  displayedColumns = [
    'clientName',
    'loanType',
    'requestedAmount',
    'status',
    'actions'
  ];

  constructor(private loanService: LoanService) {}

 ngOnInit(): void {
  this.loanService.getMyLoans().subscribe((res: any) => {
    this.loans = res.content ?? res;
  });
}


}
