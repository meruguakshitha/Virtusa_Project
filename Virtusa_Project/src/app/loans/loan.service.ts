import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class LoanService {

  private baseUrl = 'http://localhost:8081/api/loans';
  private adminUrl = 'http://localhost:8081/api/loans';

  constructor(private http: HttpClient) {}

  // USER
  getMyLoans(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}?my=true`);
  }

  create(payload: any): Observable<any> {
    return this.http.post<any>(this.baseUrl, payload);
  }

  getById(id: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/${id}`);
  }

  // ADMIN
  getAllLoans(): Observable<any> {
    return this.http.get<any>(this.adminUrl);
  }

  approveLoan(id: string): Observable<void> {
    return this.http.patch<void>(`${this.adminUrl}/${id}/approve`, {});
  }

  rejectLoan(id: string): Observable<void> {
    return this.http.patch<void>(`${this.adminUrl}/${id}/reject`, {});
  }
}
