import { Injectable } from '@angular/core';

const TOKEN_KEY = 'auth-token';
const USER_KEY = 'auth-user';

@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {
  
  constructor() { }
  
  signOut(): void {
    window.localStorage.clear();
    // Also clear session storage
    window.sessionStorage.clear();
  }
  
  public saveToken(token: string): void {
    window.localStorage.removeItem(TOKEN_KEY);
    window.localStorage.setItem(TOKEN_KEY, token);
  }
  
  public getToken(): string | null {
    return window.localStorage.getItem(TOKEN_KEY);
  }
  
  public saveUser(user: any): void {
    window.localStorage.removeItem(USER_KEY);
    window.localStorage.setItem(USER_KEY, JSON.stringify(user));
  }
  
  public getUser(): any {
    const user = window.localStorage.getItem(USER_KEY);
    if (user) {
      return JSON.parse(user);
    }
    
    return {};
  }
  
  public isLoggedIn(): boolean {
    return !!this.getToken();
  }
  
  public getUserRole(): string | null {
    const user = this.getUser();
    if (user && user.role) {
      return user.role;
    }
    return null;
  }
  
  /**
   * Check if token is about to expire
   * @param thresholdMinutes Minutes before expiration to consider the token as "about to expire"
   * @returns boolean
   */
  public isTokenAboutToExpire(thresholdMinutes: number = 5): boolean {
    const token = this.getToken();
    if (!token) return false;
    
    try {
      // Get expiration from JWT
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expiration = payload.exp * 1000; // Convert to milliseconds
      const now = Date.now();
      
      // Check if token will expire in less than threshold minutes
      return expiration - now < thresholdMinutes * 60 * 1000;
    } catch (e) {
      return false;
    }
  }
}