import { Injectable } from '@angular/core';
import { Router, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { TokenStorageService } from './token.storage';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard {
  
  constructor(
    private readonly tokenStorage: TokenStorageService,
    private readonly router: Router
  ) {}
  
  canActivate(): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    if (this.tokenStorage.isLoggedIn()) {
      return true;
    }
    
    this.router.navigate(['/login'], { queryParams: { returnUrl: this.router.url }});
    return false;
  }
}
