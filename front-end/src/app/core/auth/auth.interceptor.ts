import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse,
  HTTP_INTERCEPTORS,
} from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { catchError, filter, switchMap, take, tap } from 'rxjs/operators';
import { TokenStorageService } from './token.storage';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(
    null
  );

  constructor(
    private tokenService: TokenStorageService,
    private authService: AuthService,
    private router: Router
  ) {}

  intercept(
    request: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    let authReq = this.addTokenToRequest(request);

    return next.handle(authReq).pipe(
      catchError((error) => {
        // console.log('Error caught');
        if (error instanceof HttpErrorResponse && error.status === 401) {
          return this.handle401Error(request, next).pipe(
            catchError(() => {
              // Re-throw the original 401 error back to the component
              return throwError(() => error);
            })
          );
        }
        // console.log('Throw error');
        return throwError(() => error);
      })
    );
  }

  private addTokenToRequest(
    request: HttpRequest<unknown>
  ): HttpRequest<unknown> {
    const token = this.tokenService.getToken();

    if (token != null) {
      return request.clone({
        headers: request.headers.set('Authorization', `Bearer ${token}`),
      });
    }

    return request;
  }

  private handle401Error(
    request: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);

      // Check if we have a token to refresh
      const token = this.tokenService.getToken();
      // console.log('Handling 401');
      if (token) {
        // console.log('Have token');
        return this.authService.refreshToken().pipe(
          switchMap((response) => {
            this.isRefreshing = false;
            this.refreshTokenSubject.next(response.token);

            return next.handle(this.addTokenToRequest(request));
          }),
          catchError((err) => {
            this.isRefreshing = false;
            this.tokenService.signOut();
            this.router.navigate(['/login']);

            return throwError(() => err);
          })
        );
      } else {
        this.isRefreshing = false;
        // console.log('No token');
        // No token: this is likely a login request => let the error propagate
        return throwError(
          () => new HttpErrorResponse({ ...request, status: 401 })
        );
      }
    }

    return this.refreshTokenSubject.pipe(
      filter((token) => token != null),
      take(1),
      switchMap(() => {
        return next.handle(this.addTokenToRequest(request));
      })
    );
  }
}

export const authInterceptorProviders = [
  { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
];
