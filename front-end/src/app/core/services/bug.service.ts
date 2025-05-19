import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Bug } from '../models/bug.model';
import { PagedResponse } from '../models/paged-response.model';

@Injectable({
    providedIn: 'root'
})
export class BugService {
    private apiUrl = `${environment.apiUrl}/bugs`;

    constructor(private http: HttpClient) { }

    /**
     * Get bugs with filtering options
     */
    getBugs(
        page: number = 0,
        size: number = 10,
        difficulty?: string,
        techStackIds?: number[],
        status?: string,
        query?: string
    ): Observable<PagedResponse<Bug>> {
        let params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString())
            .set('sort', 'createdAt,desc');

        if (difficulty) {
            params = params.set('difficulty', difficulty);
        }

        if (techStackIds && techStackIds.length > 0) {
            techStackIds.forEach(id => {
                params = params.append('techStackIds', id.toString());
            });
        }

        if (status) {
            params = params.set('status', status);
        }

        if (query) {
            params = params.set('query', query);
        }

        return this.http.get<PagedResponse<Bug>>(this.apiUrl, { params });
    }

    /**
     * Get a specific bug by ID
     */
    getBugById(id: number): Observable<Bug> {
        return this.http.get<Bug>(`${this.apiUrl}/${id}`);
    }

    /**
     * Get all available difficulty levels
     */
    getDifficulties(): Observable<string[]> {
        return this.http.get<string[]>(`${this.apiUrl}/difficulties`);
    }

    /**
     * Get all available tech stacks
     */
    getTechStacks(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/tech-stacks`);
    }

    /**
    * Create a new bug
    */
    createBug(bugData: any): Observable<Bug> {
        return this.http.post<Bug>(this.apiUrl, bugData);
    }

    /**
    * Get tech stacks for bug creation form
    */
    getAllTechStacks(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/tech-stacks`);
    }

    getBugClaimerId(bugId): Observable<number> {
        return this.http.get<number>(`${this.apiUrl}/${bugId}/claimer`);
    }

    /**
    * Claim a bug
    */
    claimBug(bugId: number, note?: string): Observable<any> {
        const requestBody = note ? { bugId, claimNote: note } : { bugId };
        return this.http.post<any>(`${this.apiUrl}/${bugId}/claim`, requestBody);
    }

    unclaimBug(bugId: number): Observable<any> {
        return this.http.delete(`${this.apiUrl}/${bugId}/claim`);
    }
}