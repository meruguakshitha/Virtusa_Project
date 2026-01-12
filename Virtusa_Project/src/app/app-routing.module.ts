import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { LoginComponent } from './auth/login/login.component';
import { UserDashboardComponent } from './user/user-dashboard/user-dashboard.component';
import { AdminDashboardComponent } from './admin/admin-dashboard/admin-dashboard.component';

const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  { path: 'login', component: LoginComponent },

  {
    path: 'user',
    children: [
      { path: 'dashboard', component: UserDashboardComponent },
      { path: 'my-loans', loadComponent: () =>
          import('./user/my-loans/my-loans.component')
            .then(m => m.MyLoansComponent)
      },
      { path: 'apply-loan', loadComponent: () =>
          import('./user/apply-loan/apply-loan.component')
            .then(m => m.ApplyLoanComponent)
      }
    ]
  },

  {
    path: 'admin',
    children: [
      { path: 'dashboard', component: AdminDashboardComponent },
      { path: 'review-loans', loadComponent: () =>
          import('./admin/review-loans/review-loans.component')
            .then(m => m.ReviewLoansComponent)
      },
      { path: 'manage-users', loadComponent: () =>
          import('./admin/manage-users/manage-users.component')
            .then(m => m.ManageUsersComponent)
      }
    ]
  },

  { path: '**', redirectTo: 'login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
