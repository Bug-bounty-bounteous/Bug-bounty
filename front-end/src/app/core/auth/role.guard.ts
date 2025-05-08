import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { TokenStorageService } from './token.storage';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {

  constructor(
    private tokenStorage: TokenStorageService,
    private router: Router
  ) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    // Get required roles from route data
    const requiredRoles = route.data['roles'] as Array<string>;
    
    // Get user role from token storage
    const userRole = this.tokenStorage.getUserRole();
    
    // Check if user has the required role
    if (!userRole || (requiredRoles && !requiredRoles.includes(userRole))) {
      this.router.navigate(['/marketplace']);
      return false;
    }
    
    return true;
  }
}