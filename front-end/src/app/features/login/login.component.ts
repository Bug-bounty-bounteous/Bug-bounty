import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { NavBarComponent } from '../../layout/nav-bar/nav-bar.component';
import { ButtonComponent } from '../../shared/components/button/button.component';
import { AuthService } from '../../core/services/auth.service'; 
import { TokenStorageService } from '../../core/auth/token.storage';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, NavBarComponent, ButtonComponent],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  email: string = '';
  password: string = '';
  hasError: boolean = false;
  isSubmitting: boolean = false;

  constructor(
    private router: Router,
    private authService: AuthService,
    private tokenStorage: TokenStorageService
  ) {}

  OnEmailChange(value: string): void {
    this.email = value;
    this.hasError = false;
  }

  OnPasswordChange(value: string): void {
    this.password = value;
    this.hasError = false;
  }

  OnClickLogin(event: MouseEvent): void {
    if (!this.email || !this.password) {
      this.hasError = true;
      return;
    }
    
    this.isSubmitting = true;
    this.hasError = false;
    
    this.authService.login({
      email: this.email,
      password: this.password
    }).subscribe({
      next: (response) => {
        this.tokenStorage.saveToken(response.token);
        this.tokenStorage.saveUser({
          id: response.id,
          username: response.username,
          email: response.email,
          role: response.role
        });
        
        this.isSubmitting = false;
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.hasError = true;
        this.isSubmitting = false;
      }
    });
  }

  goToSignUp(event: MouseEvent): void {
    this.router.navigate(['/register']);
  }
}
