import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { ThemeService } from './core/services/theme.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet],
  template: `
    <router-outlet></router-outlet>
  `,
  styles: []
})
export class AppComponent implements OnInit {
  title = 'bug-bounty-app';
  
  constructor(private readonly themeService: ThemeService) {}
  
  ngOnInit() {
    // This ensures the theme is applied when the app starts
  }
}