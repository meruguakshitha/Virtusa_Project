import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { authHeaders } from '../../core/auth-header';

@Injectable({ providedIn: 'root' })
export class AdminUserService {

  private baseUrl = 'http://localhost:8081/api/admin/users';

  constructor(private http: HttpClient) {}

  // GET all users
  getAllUsers() {
    return this.http.get<any[]>(this.baseUrl, authHeaders());
  }

  // CREATE user
  createUser(user: { email: string; password: string; role: string }) {
    return this.http.post(this.baseUrl, user, authHeaders());
  }

  // ACTIVATE / DEACTIVATE
  updateStatus(id: string, active: boolean) {
    return this.http.put(
      `${this.baseUrl}/${id}/status`,
      { active },
      authHeaders()
    );
  }
}
