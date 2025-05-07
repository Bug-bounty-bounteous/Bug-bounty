import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-side-bar',
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
      link: '/marketplace',
    },
    {
      name: 'Resources',
      iconUrl: 'assets/images/book.png',
      link: '/resources',
    },
    {
      name: 'User Space',
      iconUrl: 'assets/images/user.png',
      link: '/userspace',
    },
    {
      name: 'Leaderboard',
      iconUrl: 'assets/images/bar-chart.png',
      link: '/leaderboard',
    },
  ];
}
