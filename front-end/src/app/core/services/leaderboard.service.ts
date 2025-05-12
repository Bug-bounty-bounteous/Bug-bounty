import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Leaderboard } from '../models/leaderboard.model';

@Injectable({
  providedIn: 'root',
})
export class LeaderboardService {
  private apiUrl = `${environment.apiUrl}/leaderboard`;
  top: number = 10;
  constructor(private http: HttpClient) {}

  getLeaderboard(): Observable<Leaderboard> {
    return this.http.get<Leaderboard>(`${this.apiUrl}/${this.top}`);
  }
}
