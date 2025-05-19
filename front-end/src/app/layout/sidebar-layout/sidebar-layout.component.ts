import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { TokenStorageService } from '../../core/auth/token.storage';
import { ThemeToggleComponent } from '../../shared/components/theme-toggle/theme-toggle.component';
import { ThemeService } from '../../core/services/theme.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-sidebar-layout',
  standalone: true,
  imports: [CommonModule, RouterModule, ThemeToggleComponent],
  templateUrl: './sidebar-layout.component.html',
  styleUrls: ['./sidebar-layout.component.css']
})
export class SidebarLayoutComponent {
  @Input() activeSection: string = '';
  
  userInfo: any;
  theme$: Observable<'light' | 'dark'>;
  
  constructor(
    private readonly tokenStorage: TokenStorageService,
    private readonly router: Router,
    private readonly themeService: ThemeService
  ) {
    this.userInfo = this.tokenStorage.getUser();
    this.theme$ = this.themeService.theme$;
  }
  
  logout(): void {
    this.tokenStorage.signOut();
    this.router.navigate(['/home']);
  }
}