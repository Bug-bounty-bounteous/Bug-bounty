// src/app/core/services/auth.service.ts

import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private baseUrl = 'http://localhost:8080/api/auth'; 
  constructor(private http: HttpClient) {}

  registerDeveloper(data: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/register/dev`, data);
  }

  loginDeveloper(data: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/login/dev`, data);
  }
  registerCompany(data: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/register/company`, data);
  }
  loginCompany(data: any): Observable<any> {
        return this.http.post(`${this.baseUrl}/login/company`, data);
 }     

 unlockAccount(data: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/unlock`, data, { responseType: 'text' });
  }
  
  storeToken(token: string) {
    localStorage.setItem('authToken', token);
  }

  getToken(): string | null {
    return localStorage.getItem('authToken');
  }

  logout() {
    localStorage.removeItem('authToken');
  }
}
