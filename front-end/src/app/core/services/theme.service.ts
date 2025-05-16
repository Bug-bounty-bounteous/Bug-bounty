import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private readonly THEME_KEY = 'theme-mode';
  private themeSubject = new BehaviorSubject<'light' | 'dark'>(this.getStoredTheme());
  
  public theme$ = this.themeSubject.asObservable();
  
  constructor() {
    this.applyTheme(this.themeSubject.value);
  }
  
  private getStoredTheme(): 'light' | 'dark' {
    const stored = localStorage.getItem(this.THEME_KEY);
    if (stored === 'light' || stored === 'dark') {
      return stored;
    }
    
    // Check user's system preference
    if (window.matchMedia && window.matchMedia('(prefers-color-scheme: light)').matches) {
      return 'light';
    }
    
    return 'dark'; // Default to dark theme
  }
  
  toggleTheme(): void {
    const currentTheme = this.themeSubject.value;
    const newTheme = currentTheme === 'light' ? 'dark' : 'light';
    this.setTheme(newTheme);
  }
  
  setTheme(theme: 'light' | 'dark'): void {
    this.themeSubject.next(theme);
    this.applyTheme(theme);
    localStorage.setItem(this.THEME_KEY, theme);
  }
  
  private applyTheme(theme: 'light' | 'dark'): void {
    document.documentElement.setAttribute('data-theme', theme);
  }
  
  get currentTheme(): 'light' | 'dark' {
    return this.themeSubject.value;
  }
}