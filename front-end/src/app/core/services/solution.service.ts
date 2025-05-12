import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Solution } from '../models/solution.model';

@Injectable({
  providedIn: 'root'
})
export class SolutionService {
  private apiUrl = `${environment.apiUrl}/solutions`;

  constructor(private http: HttpClient) { }

  postSolution(solutionData: any): Observable<Solution> {
    return this.http.post<Solution>(this.apiUrl, solutionData);
  }

}
