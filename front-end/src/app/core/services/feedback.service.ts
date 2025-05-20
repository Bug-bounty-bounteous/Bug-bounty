import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Feedback } from '../models/feedback.model';

@Injectable({
  providedIn: 'root'
})
export class FeedbackService {
  private apiUrl = `${environment.apiUrl}/feedback`;

  constructor(private http: HttpClient) { }

  // Service methods will be added here
  submitFeedback(payload: {
    solutionId: number;
    feedbackMessage: string;
    rating: number;
  }): Observable<any> {
    return this.http.post(`${this.apiUrl}/submit`, payload);
  }

  getFeedbacksForSolution(id: number): Observable<Feedback[]> {
    return this.http.get<Feedback[]>(`${this.apiUrl}/solution/${id}`);
  }

}
