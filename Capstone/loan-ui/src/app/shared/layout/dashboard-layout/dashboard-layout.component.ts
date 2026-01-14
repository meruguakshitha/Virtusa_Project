import { Component, OnInit } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet],
  template: `
    <!-- You will create HTML -->
    <router-outlet></router-outlet>
  `
})
export class DashboardLayoutComponent implements OnInit {

  role: 'ADMIN' | 'USER' | null = null;

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.role = localStorage.getItem('role') as 'ADMIN' | 'USER';

    // 🔒 If no role → force login
    if (!this.role) {
      this.router.navigate(['/login']);
      return;
    }

    // 🚀 Default landing per role
    if (this.router.url === '/dashboard') {
      if (this.role === 'ADMIN') {
        this.router.navigate(['/admin/review-loans']);
      } else {
        this.router.navigate(['/user/loans']);
      }
    }
  }

  isAdmin(): boolean {
    return this.role === 'ADMIN';
  }

  isUser(): boolean {
    return this.role === 'USER';
  }

  logout(): void {
    localStorage.clear();
    this.router.navigate(['/login']);
  }
}
