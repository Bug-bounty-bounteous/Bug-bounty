import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { TokenStorageService } from '../../core/auth/token.storage';
import { ThemeToggleComponent } from '../../shared/components/theme-toggle/theme-toggle.component';

@Component({
  selector: 'app-nav-bar',
  standalone: true,
  imports: [CommonModule, RouterModule, ThemeToggleComponent],
  template: `
    <nav class="navbar">
      <div class="nav-left">
        <a routerLink="/home" class="logo">Bug Bounty</a>
      </div>
      
      <div class="nav-center">
        <!-- Navigation links can go here -->
      </div>
      
      <div class="nav-right">
        <app-theme-toggle></app-theme-toggle>
        
        <div *ngIf="!isLoggedIn" class="nav-links">
          <a routerLink="/login" class="nav-link">Login</a>
          <a routerLink="/register" class="nav-link">Sign Up</a>
        </div>
        
        <div *ngIf="isLoggedIn" class="nav-links">
          <a routerLink="/dashboard" class="nav-link">Dashboard</a>
          <button (click)="logout()" class="logout-btn">Logout</button>
        </div>
      </div>
    </nav>
  `,
  styles: [`
    .navbar {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 1rem 2rem;
      background-color: var(--background-section);
      border-bottom: 1px solid var(--outline);
    }
    
    .nav-left, .nav-center, .nav-right {
      display: flex;
      align-items: center;
      gap: 1rem;
    }
    
    .logo {
      font-size: 1.5rem;
      font-weight: bold;
      color: var(--green-1);
      text-decoration: none;
    }
    
    .nav-links {
      display: flex;
      gap: 1rem;
      align-items: center;
    }
    
    .nav-link {
      color: var(--white);
      text-decoration: none;
      padding: 0.5rem 1rem;
      border-radius: 4px;
      transition: all 0.3s ease;
    }
    
    .nav-link:hover {
      background-color: var(--background-section-hover);
      color: var(--green-1);
    }
    
    .logout-btn {
      background: transparent;
      border: 1px solid var(--outline);
      color: var(--white);
      padding: 0.5rem 1rem;
      border-radius: 4px;
      cursor: pointer;
      transition: all 0.3s ease;
    }
    
    .logout-btn:hover {
      background-color: var(--red-1);
      border-color: var(--red-1);
      color: white;
    }
    
    @media (max-width: 768px) {
      .navbar {
        padding: 1rem;
      }
      
      .nav-links {
        gap: 0.5rem;
      }
    }
  `]
})
export class NavBarComponent {
  isLoggedIn: boolean = false;
  
  constructor(
    private readonly tokenStorage: TokenStorageService,
    private readonly router: Router
  ) {
    this.isLoggedIn = this.tokenStorage.isLoggedIn();
  }
  
  logout(): void {
    this.tokenStorage.signOut();
    this.router.navigate(['/home']);
  }
}