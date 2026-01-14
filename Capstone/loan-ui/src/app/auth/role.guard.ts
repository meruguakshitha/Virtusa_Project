import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {

  constructor(private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const allowedRoles = route.data['roles'] as string[];
    const userRole = localStorage.getItem('role');

    if (allowedRoles && userRole && allowedRoles.includes(userRole)) {
      return true;
    }

    // ❌ Not authorized
    this.router.navigate(['/unauthorized']);
    return false;
  }
}
