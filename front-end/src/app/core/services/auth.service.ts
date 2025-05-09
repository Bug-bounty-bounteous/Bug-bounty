import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/user.model';
import { environment } from '../../../environments/environment';
import { TokenStorageService } from '../auth/token.storage';
import { Router } from '@angular/router';

const AUTH_API = `${environment.apiUrl}/auth`;
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private userSubject = new BehaviorSubject<any>(null);
  public user$ = this.userSubject.asObservable();
  private loginAttempts = 0;
  private readonly MAX_ATTEMPTS = 3;
  private readonly LOCKOUT_DURATION = 15 * 60 * 1000; // 15 minutes
  private lockoutTime: number | null = null;

  constructor(
    private readonly http: HttpClient,
    private readonly tokenStorage: TokenStorageService,
    private readonly router: Router
  ) {
    const user = this.tokenStorage.getUser();
    if (user && Object.keys(user).length > 0) {
      this.userSubject.next(user);
    }
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    if (this.isLockedOut()) {
      return new Observable(observer => {
        observer.error({ status: 423, error: { message: 'Account is temporarily locked. Please try again later.' } });
        observer.complete();
      });
    }

    return this.http.post<AuthResponse>(`${AUTH_API}/login`, credentials, httpOptions).pipe(
      tap({
        next: (response) => {
          this.loginAttempts = 0;
          this.lockoutTime = null;
          localStorage.removeItem('loginAttempts');
          localStorage.removeItem('lockoutTime');

          this.tokenStorage.saveToken(response.token);
          this.tokenStorage.saveUser({
            id: response.id,
            username: response.username,
            email: response.email,
            role: response.role
          });

          this.userSubject.next({
            id: response.id,
            username: response.username,
            email: response.email,
            role: response.role
          });
        },
        error: (error) => {
          console.error('Error in login service:', error);
        }
      })
    );
  }

  unlockAccount(email: string, captchaCode: string): Observable<any> {
    return this.http.post(`${AUTH_API}/unlock`, { email, captchaCode }, httpOptions);
  }

  register(userData: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${AUTH_API}/register`, userData, httpOptions).pipe(
      tap(response => {
        this.tokenStorage.saveToken(response.token);
        this.tokenStorage.saveUser({
          id: response.id,
          username: response.username,
          email: response.email,
          role: response.role
        });
        this.userSubject.next({
          id: response.id,
          username: response.username,
          email: response.email,
          role: response.role
        });
      })
    );
  }

  refreshToken(): Observable<AuthResponse> {
    return this.http.get<AuthResponse>(`${AUTH_API}/refresh-token`, {
      headers: new HttpHeaders({ 'Authorization': `Bearer ${this.tokenStorage.getToken()}` })
    }).pipe(
      tap(response => this.tokenStorage.saveToken(response.token))
    );
  }

  logout(): void {
    this.tokenStorage.signOut();
    this.userSubject.next(null);
    setTimeout(() => this.router.navigate(['/login']), 0);

    const token = this.tokenStorage.getToken();
    if (!token) return;

    this.http.post<any>(`${AUTH_API}/logout`, {}, httpOptions).subscribe({
      next: () => console.log('Logout successful on server'),
      error: (err) => console.error('Logout error on server:', err),
      complete: () => console.log('Logout request completed')
    });
  }

  logoutLocally(): void {
    this.tokenStorage.signOut();
    this.userSubject.next(null);
    this.router.navigate(['/login']);
  }

  handleLoginFailure(): void {
    this.loginAttempts++;
    localStorage.setItem('loginAttempts', this.loginAttempts.toString());

    if (this.loginAttempts >= this.MAX_ATTEMPTS) {
      this.lockoutTime = Date.now() + this.LOCKOUT_DURATION;
      localStorage.setItem('lockoutTime', this.lockoutTime.toString());
    }
  }

  isLockedOut(): boolean {
    const storedAttempts = localStorage.getItem('loginAttempts');
    if (storedAttempts) {
      this.loginAttempts = parseInt(storedAttempts, 10);
    }

    const storedLockoutTime = localStorage.getItem('lockoutTime');
    if (storedLockoutTime) {
      this.lockoutTime = parseInt(storedLockoutTime, 10);
    }

    if (this.lockoutTime && Date.now() < this.lockoutTime) {
      return true;
    } else if (this.lockoutTime && Date.now() >= this.lockoutTime) {
      this.loginAttempts = 0;
      this.lockoutTime = null;
      localStorage.removeItem('loginAttempts');
      localStorage.removeItem('lockoutTime');
    }

    return false;
  }

  isLoggedIn(): boolean {
    return this.tokenStorage.isLoggedIn();
  }

  getRemainingLockoutTime(): number {
    if (!this.lockoutTime) return 0;
    return Math.max(0, this.lockoutTime - Date.now());
  }
}
