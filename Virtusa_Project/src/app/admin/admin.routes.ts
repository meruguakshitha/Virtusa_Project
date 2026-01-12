import { Routes } from '@angular/router';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { ReviewLoansComponent } from './review-loans/review-loans.component';
import { ManageUsersComponent } from './manage-users/manage-users.component';

export const ADMIN_ROUTES: Routes = [
  {
    path: '',
    component: AdminDashboardComponent,
    children: [
      { path: 'review-loans', component: ReviewLoansComponent },
      { path: 'manage-users', component: ManageUsersComponent },
      { path: '', redirectTo: 'review-loans', pathMatch: 'full' }
    ]
  }
];
