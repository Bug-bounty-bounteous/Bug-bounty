import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LeaderboardService {
  private apiUrl = `${environment.apiUrl}/leaderboard`;

  constructor(private http: HttpClient) { }

  getLeaderboard(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  getDeveloperRanking(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/developers/${id}`);
  }

  getFilteredLeaderboard(filter: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}?filter=${filter}`);
  }
}
