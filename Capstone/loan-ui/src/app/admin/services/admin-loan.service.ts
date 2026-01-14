/*import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class AdminLoanService {

  private baseUrl = 'http://localhost:8081/api/admin/loans';

  constructor(private http: HttpClient) {}

  // ✅ FIXED: let interceptor attach JWT
  getLoans() {
    return this.http.get<any>(this.baseUrl);
  }

  approveLoan(id: string) {
    return this.http.patch(
      `${this.baseUrl}/${id}/approve`,
      {}
    );
  }

  rejectLoan(id: string) {
    return this.http.patch(
      `${this.baseUrl}/${id}/reject`,
      {}
    );
  }
} */


 

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { authHeaders } from '../../core/auth-header';

@Injectable({ providedIn: 'root' })
export class AdminLoanService {

  private baseUrl = 'http://localhost:8081/api/admin/loans';

  constructor(private http: HttpClient) {}

  // ✅ paginated response
  getLoans() {
    return this.http.get<any>(this.baseUrl, authHeaders());
  }

  // ✅ document-compliant status change
  changeStatus(
    id: string,
    status: 'UNDER_REVIEW' | 'APPROVED' | 'REJECTED',
    comments: string
  ) {
    return this.http.patch(
      `${this.baseUrl}/${id}/status`,
      { status, comments },
      authHeaders()
    );
  }
}
