import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class AdminUserService {

  private baseUrl = 'http://localhost:8081/api/admin/users';

  constructor(private http: HttpClient) {}

  // ✅ FIXED
  getAllUsers() {
    return this.http.get<any[]>(this.baseUrl);
  }

  createUser(user: { email: string; password: string; role: string }) {
    return this.http.post(this.baseUrl, user);
  }

  updateStatus(id: string, active: boolean) {
    return this.http.put(
      `${this.baseUrl}/${id}/status`,
      { active }
    );
  }
}
