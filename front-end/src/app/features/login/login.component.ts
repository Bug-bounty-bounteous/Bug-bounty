import { Component } from '@angular/core';
import { NavBarComponent } from '../../layout/nav-bar/nav-bar.component';
import { CommonModule } from '@angular/common';
import { InputBarComponent } from '../../shared/components/input-bar/input-bar.component';
import { ButtonComponent } from '../../shared/components/button/button.component';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    NavBarComponent,
    CommonModule,
    InputBarComponent,
    ButtonComponent,
    FormsModule,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  username: string = '';
  password: string = '';

  hasError: boolean = false;

  constructor(private router: Router) {}

  goToSignUp(event: MouseEvent) {
    this.router.navigate(['/signup']);
  }

  OnUsernameChange(value: string) {
    this.username = value;
  }

  OnPasswordChange(value: string) {
    this.password = value;
  }

  OnClickLogin(event: MouseEvent) {
    console.log('Login logic here...');
  }
}
