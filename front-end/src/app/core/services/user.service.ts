import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Solution } from '../models/solution.model';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  getCurrentUserProfile(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/me`);
  }

  getClaimedBugs(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/me/claimed-bugs`);
  }

  getUploadedBugs(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/me/uploaded-bugs`);
  }
}
