import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { NavBarComponent } from '../../layout/nav-bar/nav-bar.component';
import { ButtonComponent } from '../../shared/components/button/button.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  imports: [CommonModule, NavBarComponent, ButtonComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent {
  imageUrl: string = 'assets/images/coding.jpg';

  constructor(private router: Router) {}

  clickButtonTest(e: MouseEvent) {
    this.router.navigate(['/login']);
  }
}
