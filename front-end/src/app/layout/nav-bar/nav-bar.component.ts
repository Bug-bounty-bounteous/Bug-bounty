import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-nav-bar',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './nav-bar.component.html',
  styleUrl: './nav-bar.component.css',
})
export class NavBarComponent {
  logoUrl: string = 'assets/images/logo.png';

  links: { name: string; url: string }[] = [
    { name: 'About', url: '/about' },
    { name: 'Login', url: '/login' },
  ];
}
