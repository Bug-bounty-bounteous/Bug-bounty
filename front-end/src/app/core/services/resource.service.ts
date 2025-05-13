import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LearningResource } from '../models/resource.model';
import { PagedResponse } from '../models/paged-response.model';

@Injectable({
  providedIn: 'root'
})
export class ResourceService {
  private apiUrl = `${environment.apiUrl}/resources`;

  constructor(private http: HttpClient) { }

  /**
   * Get all learning resources with optional filtering
   */
  getResources(
    page: number = 0,
    size: number = 10,
    type?: string,
    query?: string
  ): Observable<PagedResponse<LearningResource>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', 'date,desc');

    if (type) {
      params = params.set('type', type);
    }

    if (query) {
      params = params.set('query', query);
    }

    return this.http.get<PagedResponse<LearningResource>>(this.apiUrl, { params });
  }

  /**
   * Get a specific resource by ID
   */
  getResourceById(id: number): Observable<LearningResource> {
    return this.http.get<LearningResource>(`${this.apiUrl}/${id}`);
  }

  /**
   * Get all available resource types
   */
  getResourceTypes(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/types`);
  }

  /**
   * Create a new learning resource
   */
  createResource(resource: any): Observable<LearningResource> {
    return this.http.post<LearningResource>(this.apiUrl, resource);
  }

  /**
   * Update an existing resource
   */
  updateResource(id: number, resource: any): Observable<LearningResource> {
    return this.http.put<LearningResource>(`${this.apiUrl}/${id}`, resource);
  }

  /**
   * Report a resource as outdated or broken
   */
  reportResource(id: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/${id}/report`, {});
  }

  /**
   * Get reported resources (admin only)
   */
  getReportedResources(): Observable<LearningResource[]> {
    return this.http.get<LearningResource[]>(`${this.apiUrl}/reported`);
  }

  /**
   * Clear report flag on a resource (admin only)
   */
  clearResourceReport(id: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/${id}/clear-report`, {});
  }

  /**
   * Get resources by company
   */
  getResourcesByCompany(): Observable<LearningResource[]> {
    return this.http.get<LearningResource[]>(`${this.apiUrl}/company`);
  }
}