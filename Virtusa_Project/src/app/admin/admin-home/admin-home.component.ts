import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  standalone: true,
  selector: 'app-admin-home',
  imports: [CommonModule],
  template: `
    <h1>Admin Dashboard</h1>
    <p>Welcome to the Loan Approval System</p>
  `
})
export class AdminHomeComponent {}
