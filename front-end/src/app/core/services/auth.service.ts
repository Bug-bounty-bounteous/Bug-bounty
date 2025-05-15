import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
} from '../models/user.model';
import { environment } from '../../../environments/environment';
import { TokenStorageService } from '../auth/token.storage';
import { Router } from '@angular/router';

const AUTH_API = `${environment.apiUrl}/auth`;
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
};

@Injectable({
  providedIn: 'root',
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
      return new Observable((observer) => {
        observer.error({
          status: 423,
          error: {
            message: 'Account is temporarily locked. Please try again later.',
          },
        });
        observer.complete();
      });
    }

    return this.http
      .post<AuthResponse>(`${AUTH_API}/login`, credentials, httpOptions)
      .pipe(
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
              role: response.role,
            });

            this.userSubject.next({
              id: response.id,
              username: response.username,
              email: response.email,
              role: response.role,
            });
          },
          error: (error) => {
            // console.error('Error in login service:', error);
          },
        })
        // catchError((error) => {
        //   console.error('Caught Error in login service:', error);
        //   return throwError(() => error); // re-throw so subscriber can handle it
        // })
      );
  }

  unlockAccount(email: string, captchaCode: string): Observable<any> {
    return this.http.post(
      `${AUTH_API}/unlock`,
      { email, captchaCode },
      httpOptions
    );
  }

  SolveCaptchaRequest(captcha: string): Observable<any> {
    return this.http.post(
      `${AUTH_API}/verify-captcha`,
      { captcha },
      httpOptions
    );
  }

  register(userData: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${AUTH_API}/register`, userData, httpOptions)
      .pipe(
        tap((response) => {
          this.tokenStorage.saveToken(response.token);
          this.tokenStorage.saveUser({
            id: response.id,
            username: response.username,
            email: response.email,
            role: response.role,
          });
          this.userSubject.next({
            id: response.id,
            username: response.username,
            email: response.email,
            role: response.role,
          });
        })
      );
  }

  refreshToken(): Observable<AuthResponse> {
    return this.http
      .get<AuthResponse>(`${AUTH_API}/refresh-token`, {
        headers: new HttpHeaders({
          Authorization: `Bearer ${this.tokenStorage.getToken()}`,
        }),
      })
      .pipe(tap((response) => this.tokenStorage.saveToken(response.token)));
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
      complete: () => console.log('Logout request completed'),
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

  /**
 * Initiate OAuth2 authentication with Google
 */
  initGoogleLogin(): void {
    const authUrl = `${environment.apiUrl}/oauth2/authorization/google`;
    window.location.href = authUrl;
  }

  /**
   * Handle OAuth2 callback from backend
   */
  handleOAuth2Callback(token: string): Observable<AuthResponse> {
    return new Observable(observer => {
      try {
        // Save the token directly from backend
        this.tokenStorage.saveToken(token);

        // Decode JWT to extract user information
        const payload = JSON.parse(atob(token.split('.')[1]));

        const authResponse: AuthResponse = {
          token: token,
          type: 'Bearer',
          id: payload.userId || payload.sub,
          username: payload.preferred_username || payload.email,
          email: payload.email,
          role: payload.role || payload.authorities?.[0]?.replace('ROLE_', '')
        };

        // Save user information
        this.tokenStorage.saveUser({
          id: authResponse.id,
          username: authResponse.username,
          email: authResponse.email,
          role: authResponse.role,
        });

        // Update user subject
        this.userSubject.next({
          id: authResponse.id,
          username: authResponse.username,
          email: authResponse.email,
          role: authResponse.role,
        });

        observer.next(authResponse);
        observer.complete();
      } catch (error) {
        console.error('Error processing OAuth2 callback:', error);
        observer.error(error);
      }
    });
  }

  /**
 * Handle OAuth2 callback with additional user info
 */
handleOAuth2CallbackWithInfo(token: string, userInfo: any): Observable<AuthResponse> {
  return new Observable(observer => {
    try {
      // Save the token directly from backend
      this.tokenStorage.saveToken(token);
      
      // Use provided user info instead of decoding JWT
      const authResponse: AuthResponse = {
        token: token,
        type: 'Bearer',
        id: userInfo.id,
        username: userInfo.username,
        email: userInfo.email,
        role: userInfo.role
      };
      
      // Save user information
      this.tokenStorage.saveUser({
        id: authResponse.id,
        username: authResponse.username,
        email: authResponse.email,
        role: authResponse.role,
      });
      
      // Update user subject
      this.userSubject.next({
        id: authResponse.id,
        username: authResponse.username,
        email: authResponse.email,
        role: authResponse.role,
      });
      
      console.log('OAuth2 user saved:', authResponse);
      
      observer.next(authResponse);
      observer.complete();
    } catch (error) {
      console.error('Error processing OAuth2 callback:', error);
      observer.error(error);
    }
  });
}
}
