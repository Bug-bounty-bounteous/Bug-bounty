import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-side-bar',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './side-bar.component.html',
  styleUrl: './side-bar.component.css',
})
export class SideBarComponent {
  logoUrl: string = 'assets/images/logo.png';
  @Input() activeSection: string = '';

  sections: { name: string; iconUrl: string; link: string }[] = [
    {
      name: 'Bug Marketplace',
      iconUrl: 'assets/images/bug.png',
      link: '/bugs', // Updated to point to the new bug list component
    },
    {
      name: 'Resources',
      iconUrl: 'assets/images/book.png',
      link: '/resources',
    },
    {
      name: 'User Space',
      iconUrl: 'assets/images/user.png',
      link: '/dashboard',
    },
    {
      name: 'Leaderboard',
      iconUrl: 'assets/images/bar-chart.png',
      link: '/leaderboard',
    },
  ];
  
  constructor(private authService: AuthService) {}
  
  logout(): void {
    this.authService.logout();
  }
}