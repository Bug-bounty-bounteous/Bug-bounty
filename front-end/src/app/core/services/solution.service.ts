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
  
  getSolutionsByDeveloper(developerId: number): Observable<Solution[]> {
  return this.http.get<Solution[]>(`${this.apiUrl}/developer/${developerId}`);
}
getSolutionsByCompany(): Observable<any[]> {
  return this.http.get<any[]>(`${this.apiUrl}/company`);
}

  postSolution(solutionData: any): Observable<Solution> {
    return this.http.post<Solution>(this.apiUrl, solutionData);
  }

}
