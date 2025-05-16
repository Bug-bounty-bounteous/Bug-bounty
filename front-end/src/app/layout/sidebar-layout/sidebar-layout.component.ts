import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { TokenStorageService } from '../../core/auth/token.storage';
import { ThemeToggleComponent } from '../../shared/components/theme-toggle/theme-toggle.component';

@Component({
  selector: 'app-sidebar-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, ThemeToggleComponent],
  template: `
    <div class="layout-container">
      <!-- Sidebar -->
      <aside class="sidebar">
        <div class="sidebar-header">
          <h2 class="sidebar-title">Bug Bounty</h2>
          <app-theme-toggle></app-theme-toggle>
        </div>
        
        <nav class="sidebar-nav">
          <a 
            routerLink="/dashboard" 
            class="sidebar-link"
            [class.active]="activeSection === 'User Space'"
          >
            <span class="sidebar-icon">🏠</span>
            Dashboard
          </a>
          
          <a 
            routerLink="/marketplace" 
            class="sidebar-link"
            [class.active]="activeSection === 'Bug Marketplace'"
          >
            <span class="sidebar-icon">🐛</span>
            Bug Marketplace
          </a>
          
          <a 
            routerLink="/leaderboard" 
            class="sidebar-link"
            [class.active]="activeSection === 'Leaderboard'"
          >
            <span class="sidebar-icon">🏆</span>
            Leaderboard
          </a>
          
          <a 
            routerLink="/resources" 
            class="sidebar-link"
            [class.active]="activeSection === 'Resources'"
          >
            <span class="sidebar-icon">📚</span>
            Learning Resources
          </a>
   
        </nav>
        
        <div class="sidebar-footer">
          <div class="user-info" *ngIf="userInfo">
            <div class="user-name">{{ userInfo.username }}</div>
            <div class="user-role">{{ userInfo.role }}</div>
          </div>
          <button class="logout-btn" (click)="logout()">
            <span class="sidebar-icon">🚪</span>
            Logout
          </button>
        </div>
      </aside>
      
      <!-- Main Content -->
      <main class="main-content">
        <div class="content-wrapper">
          <ng-content></ng-content>
        </div>
      </main>
    </div>
  `,
  styles: [`
    .layout-container {
      display: flex;
      min-height: 100vh;
      background-color: var(--background);
    }
    
    .sidebar {
      width: 280px;
      background-color: var(--background-section);
      border-right: 1px solid var(--outline);
      display: flex;
      flex-direction: column;
      position: fixed;
      height: 100vh;
      overflow-y: auto;
    }
    
    .sidebar-header {
      padding: 1.5rem;
      border-bottom: 1px solid var(--outline);
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    
    .sidebar-title {
      color: var(--green-1);
      margin: 0;
      font-size: 1.25rem;
    }
    
    .sidebar-nav {
      flex: 1;
      padding: 1rem 0;
    }
    
    .sidebar-link {
      display: flex;
      align-items: center;
      gap: 1rem;
      padding: 1rem 1.5rem;
      color: var(--white);
      text-decoration: none;
      transition: all 0.3s ease;
      border-left: 3px solid transparent;
    }
    
    .sidebar-link:hover {
      background-color: var(--background-section-hover);
      color: var(--green-1);
    }
    
    .sidebar-link.active {
      background-color: var(--green-6);
      color: var(--green-1);
      border-left-color: var(--green-1);
    }
    
    .sidebar-icon {
      font-size: 1.1rem;
      width: 20px;
      text-align: center;
    }
    
    .sidebar-footer {
      border-top: 1px solid var(--outline);
      padding: 1rem;
    }
    
    .user-info {
      padding: 1rem;
      background-color: var(--background);
      border-radius: 8px;
      margin-bottom: 1rem;
    }
    
    .user-name {
      font-weight: bold;
      color: var(--green-1);
      margin-bottom: 0.25rem;
    }
    
    .user-role {
      font-size: 0.875rem;
      color: var(--white);
      opacity: 0.7;
      text-transform: uppercase;
    }
    
    .logout-btn {
      width: 100%;
      display: flex;
      align-items: center;
      gap: 1rem;
      padding: 1rem;
      background: transparent;
      border: 1px solid var(--outline);
      color: var(--white);
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.3s ease;
    }
    
    .logout-btn:hover {
      background-color: var(--red-1);
      border-color: var(--red-1);
      color: white;
    }
    
    .main-content {
      flex: 1;
      margin-left: 280px;
      background-color: var(--background);
    }
    
    .content-wrapper {
      padding: 2rem;
      min-height: 100vh;
    }
    
    /* Light mode specific adjustments */
    :root[data-theme="light"] .sidebar-link.active {
      background-color: #e8f5e9;
      color: var(--green-1);
    }
    
    :root[data-theme="light"] .user-info {
      background-color: #f8f9fa;
    }
    
    /* Mobile responsiveness */
    @media (max-width: 768px) {
      .sidebar {
        transform: translateX(-100%);
        transition: transform 0.3s ease;
      }
      
      .sidebar.open {
        transform: translateX(0);
      }
      
      .main-content {
        margin-left: 0;
      }
      
      .content-wrapper {
        padding: 1rem;
      }
    }
  `]
})
export class SidebarLayoutComponent {
  @Input() activeSection: string = '';
  
  userInfo: any;
  
  constructor(
    private readonly tokenStorage: TokenStorageService,
    private readonly router: Router
  ) {
    this.userInfo = this.tokenStorage.getUser();
  }
  
  logout(): void {
    this.tokenStorage.signOut();
    this.router.navigate(['/home']);
  }
}