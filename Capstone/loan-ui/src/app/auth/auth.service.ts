import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private loginUrl = 'http://localhost:8081/api/auth/login';

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  // ✅ LOGIN API
  login(email: string, password: string) {
    return this.http.post<any>(this.loginUrl, {
      email,
      password
    });
  }

  // ✅ SAVE TOKEN + REDIRECT
  handleLoginSuccess(res: any) {
    localStorage.setItem('token', res.accessToken);
    localStorage.setItem('role', res.role);

    if (res.role === 'ADMIN') {
      this.router.navigate(['/admin']);
    } else {
      this.router.navigate(['/user/dashboard']);
    }
  }

  // ✅ USED BY GUARDS
  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  get role(): string | null {
    return localStorage.getItem('role');
  }

  logout() {
    localStorage.clear();
    this.router.navigate(['/login']);
  }

  // ✅ CHECK ADMIN ROLE
isAdmin(): boolean {
  return localStorage.getItem('role') === 'ADMIN';
}

// ✅ CHECK USER ROLE (optional but useful)
isUser(): boolean {
  return localStorage.getItem('role') === 'USER';
}
}
