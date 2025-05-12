import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { NavBarComponent } from '../../layout/nav-bar/nav-bar.component';
import { ButtonComponent } from '../../shared/components/button/button.component';
import { AuthService } from '../../core/services/auth.service';
import { TokenStorageService } from '../../core/auth/token.storage';

@Component({
  selector: 'app-sign-up',
  standalone: true,
  imports: [CommonModule, FormsModule, NavBarComponent, ButtonComponent],
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.css']
})
export class SignUpComponent {
  firstName: string = '';
  lastName: string = '';
  username: string = '';
  email: string = '';
  password: string = '';
  confirmPassword: string = '';
  isCompany: boolean = false;
  
  invalidFirstName: boolean = false;
  invalidLastName: boolean = false;
  invalidUsername: boolean = false;
  invalidEmail: boolean = false;
  invalidPassword: boolean = false;
  invalidConfirmPassword: boolean = false;
  isSubmitting: boolean = false;
  
  constructor(
    private router: Router,
    private authService: AuthService,
    private tokenStorage: TokenStorageService
  ) {}
  
  OnChangeFirstName(value: string): void {
    this.firstName = value;
    this.invalidFirstName = value.length < 2;
  }
  
  OnChangeLastName(value: string): void {
    this.lastName = value;
    this.invalidLastName = value.length < 2;
  }
  
  OnChangeUsername(value: string): void {
    this.username = value;
    this.invalidUsername = value.length < 3;
  }
  
  OnChangeEmail(value: string): void {
    this.email = value;
    const emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
    this.invalidEmail = !emailPattern.test(value);
  }
  
  OnChangePassword(value: string): void {
    this.password = value;
    this.invalidPassword = value.length < 6;
    if (this.confirmPassword) {
      this.invalidConfirmPassword = this.password !== this.confirmPassword;
    }
  }
  
  OnChangeConfirmPassword(value: string): void {
    this.confirmPassword = value;
    this.invalidConfirmPassword = this.password !== value;
  }
  
  OnCompanyChange(value: boolean): void {
    this.isCompany = value;
  }
  
  OnClickSignUp(event: MouseEvent): void {
    // Check all validations first
    if (!this.isCompany) {
      this.invalidFirstName = this.firstName.length < 2;
      this.invalidLastName = this.lastName.length < 2;
    } else {
      this.invalidFirstName = false;
      this.invalidLastName = false;
    }
    this.invalidUsername = this.username.length < 3;
    const emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
    this.invalidEmail = !emailPattern.test(this.email);
    this.invalidPassword = this.password.length < 6;
    this.invalidConfirmPassword = this.password !== this.confirmPassword;
    
    if (this.invalidFirstName || this.invalidLastName || this.invalidUsername ||
        this.invalidEmail || this.invalidPassword || this.invalidConfirmPassword) {
      return; // Stop if there are any validation errors
    }
    
    this.isSubmitting = true;
    
    this.authService.register({
    firstName: this.firstName,
    lastName: this.lastName,
    username: this.username,
    email: this.email,
    password: this.password,
    role: this.isCompany ? 'COMPANY' : 'DEVELOPER'
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
      this.invalidEmail = true;
      this.isSubmitting = false;
    }
  });

  }
}
