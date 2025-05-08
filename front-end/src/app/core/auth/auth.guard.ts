import { Injectable } from '@angular/core';
import { Router, UrlTree, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable, of } from 'rxjs';
import { TokenStorageService } from './token.storage';
import { AuthService } from '../services/auth.service';
import { catchError, map, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard {
  
  constructor(
    private readonly tokenStorage: TokenStorageService,
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}
  
  canActivate(
    route: ActivatedRouteSnapshot, 
    state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    // Check if user is logged in
    if (!this.tokenStorage.isLoggedIn()) {
      this.router.navigate(['/login'], { queryParams: { returnUrl: state.url }});
      return false;
    }
    
    // Check if token is about to expire (less than 5 minutes)
    if (this.tokenStorage.isTokenAboutToExpire()) {
      // Try to refresh the token
      return this.authService.refreshToken().pipe(
        map(() => true),
        catchError(() => {
          // If refresh fails, redirect to login
          this.router.navigate(['/login'], { queryParams: { returnUrl: state.url }});
          return of(false);
        })
      );
    }
    
    return true;
  }
}