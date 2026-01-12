import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  standalone: true,
  selector: 'app-user-home',
  imports: [CommonModule],
  template: `
    <h1>Welcome to Loan Approval System</h1>
    <p>Apply loans, track status, and view EMI details easily.</p>
  `
})
export class UserHomeComponent {}
