import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ThemeService } from '../../../core/services/theme.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-theme-toggle',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './theme-toggle.component.html',
  styleUrls: ['./theme-toggle.component.css']
})
export class ThemeToggleComponent {
  theme$: Observable<'light' | 'dark'>;
  
  constructor(private readonly themeService: ThemeService) {
    this.theme$ = this.themeService.theme$;
  }
  
  toggleTheme(): void {
    this.themeService.toggleTheme();
  }
}