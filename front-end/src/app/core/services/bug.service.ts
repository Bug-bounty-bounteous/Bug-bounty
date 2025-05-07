import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Bug } from '../models/bug.model';

@Injectable({
  providedIn: 'root'
})
export class BugService {
  private apiUrl = `${environment.apiUrl}/bugs`;

  constructor(private http: HttpClient) { }

  // Service methods will be added here
}
