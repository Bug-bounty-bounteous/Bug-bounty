import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
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


  getSolutionsByCompany(companyId: number): Observable<Solution[]> {
    return this.http.get<Solution[]>(`${this.apiUrl}/company/${companyId}`);
  }

  postSolution(solutionData: any): Observable<number> {
    return this.http.post<number>(this.apiUrl, solutionData);
  }

  getSolutionById(solutionId: number): Observable<Solution> {
    return this.http.get<Solution>(`${this.apiUrl}/${solutionId}`);
  }

  downloadSolutionFile(solutionId: number) {
    window.open(`${this.apiUrl}/${solutionId}/file`, '_blank');
  }

  refuseSolution(id: number): Observable<any> {
    return this.http.post<Solution>(`${this.apiUrl}/${id}/verdict`, "REJECTED");
  }

  acceptSolution(id: number): Observable<any> {
    return this.http.post<Solution>(`${this.apiUrl}/${id}/verdict`, "ACCEPTED");
  }
}
