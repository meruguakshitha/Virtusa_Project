import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { UserDashboardComponent } from './user/user-dashboard/user-dashboard.component';
import { MyLoansComponent } from './user/my-loans/my-loans.component';
import { ApplyLoanComponent } from './user/apply-loan/apply-loan.component';
import { AdminDashboardComponent } from './admin/admin-dashboard/admin-dashboard.component';
import { ManageUsersComponent } from './admin/manage-users/manage-users.component';
import { ReviewLoansComponent } from './admin/review-loans/review-loans.component'; 
import { AdminHomeComponent } from './admin/admin-home/admin-home.component';

export const routes: Routes = [

  // 🔁 Default redirect
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },

  // 🔐 Login
  {
    path: 'login',
    loadComponent: () =>
      import('./auth/login/login.component')
        .then(m => m.LoginComponent)
  },

  // 👤 USER DASHBOARD (UNCHANGED)
  {
    path: 'user/dashboard',
    loadComponent: () =>
      import('./user/user-dashboard/user-dashboard.component')
        .then(m => m.UserDashboardComponent),
    children: [

      {
        path: '',
        redirectTo: 'my-loans',
        pathMatch: 'full'
      },

      {
        path: 'my-loans',
        loadComponent: () =>
          import('./user/my-loans/my-loans.component')
            .then(m => m.MyLoansComponent)
      },

      {
        path: 'apply-loan',
        loadComponent: () =>
          import('./user/apply-loan/apply-loan.component')
            .then(m => m.ApplyLoanComponent)
      }
    ]
  },

  // ============================
  // 🛠️ ADMIN ROUTES (ONLY ADD)
  // ============================

  
{
    path: 'admin',
    loadComponent: () =>
      import('./admin/admin-dashboard/admin-dashboard.component')
        .then(m => m.AdminDashboardComponent),
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./admin/admin-home/admin-home.component')
            .then(m => m.AdminHomeComponent)
      },
      {
        path: 'dashboard',
        redirectTo: '',
        pathMatch: 'full'
      },
      {
        path: 'manage-users',
        loadComponent: () =>
          import('./admin/manage-users/manage-users.component')
            .then(m => m.ManageUsersComponent)
      },
      {
        path: 'review-loans',
        loadComponent: () =>
          import('./admin/review-loans/review-loans.component')
            .then(m => m.ReviewLoansComponent)
      }
    ]
  }



];

