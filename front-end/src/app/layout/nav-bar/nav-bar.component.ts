import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { TokenStorageService } from '../../core/auth/token.storage';
import { ThemeToggleComponent } from '../../shared/components/theme-toggle/theme-toggle.component';
import { ThemeService } from '../../core/services/theme.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-nav-bar',
  standalone: true,
  imports: [CommonModule, RouterModule, ThemeToggleComponent],
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.css']
})
export class NavBarComponent {
  isLoggedIn: boolean = false;
  theme$: Observable<'light' | 'dark'>;
  
  constructor(
    private readonly tokenStorage: TokenStorageService,
    private readonly router: Router,
    private readonly themeService: ThemeService
  ) {
    this.isLoggedIn = this.tokenStorage.isLoggedIn();
    this.theme$ = this.themeService.theme$;
  }
  
  logout(): void {
    this.tokenStorage.signOut();
    this.router.navigate(['/home']);
  }
}