import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { authHeaders } from '../../core/auth-header';

@Injectable({ providedIn: 'root' })
export class AdminLoanService {

  private baseUrl = 'http://localhost:8081/api/admin/loans';

  constructor(private http: HttpClient) {}

  // GET paginated loans
 getLoans() {
    return this.http.get<any[]>(this.baseUrl);
  }


  // APPROVE / REJECT
 approveLoan(id: string) {
    return this.http.patch(`${this.baseUrl}/${id}/approve`, {});
  }

  rejectLoan(id: string) {
    return this.http.patch(`${this.baseUrl}/${id}/reject`, {});
  }
}

  // DELETE (Soft delete)
 
 

