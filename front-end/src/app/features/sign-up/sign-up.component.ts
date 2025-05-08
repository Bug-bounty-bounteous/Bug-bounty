import { Component } from '@angular/core';
import { NavBarComponent } from '../../layout/nav-bar/nav-bar.component';
import { CommonModule } from '@angular/common';
import { InputBarComponent } from '../../shared/components/input-bar/input-bar.component';
import { FormsModule } from '@angular/forms';
import { ButtonComponent } from '../../shared/components/button/button.component';
import { AuthService } from '../../core/services/authservice';
import { Router } from '@angular/router';

@Component({
  selector: 'app-sign-up',
  imports: [
    NavBarComponent,
    CommonModule,
    InputBarComponent,
    FormsModule,
    ButtonComponent,
  ],
  templateUrl: './sign-up.component.html',
  styleUrl: './sign-up.component.css',
})
export class SignUpComponent {
  invalidEmail: boolean = false;
  invalidPassword: boolean = false;
  invalidConfirmPassword: boolean = false;

  email: string = '';
  password: string = '';
  confirmPassword: string = '';
  companyName: string = '';
  isCompany: boolean = false;
  
  constructor(private authService: AuthService, private router: Router) {}

  OnChangeEmail(value: string) {
    this.email = value;
    this.invalidEmail = false;
  }

  OnChangePassword(value: string) {
    this.password = value;
    this.invalidPassword = false;
  }

  OnChangeConfirmPassword(value: string) {
    this.confirmPassword = value;
    this.invalidConfirmPassword = false;
  }

  OnClickSignUp(event: MouseEvent) {
    event.preventDefault();
  
    if (this.password !== this.confirmPassword) {
      this.invalidConfirmPassword = true;
      return;
    }
  
    if (this.isCompany) {
      const payload = {
        email: this.email,
        plainPassword: this.password,
        companyName: this.companyName
      };
  
      this.authService.registerCompany(payload).subscribe({
        next: () => {
          alert('Company registration successful!');
          this.router.navigate(['/login']);
        },
        error: (err) => {
          alert('Company registration failed: ' + err.error);
        }
      });
    } else {
      const payload = {
        name: this.email.split('@')[0],
        email: this.email,
        plainPassword: this.password
      };
  
      this.authService.registerDeveloper(payload).subscribe({
        next: () => {
          alert('Developer registration successful!');
          this.router.navigate(['/login']);
        },
        error: (err) => {
          alert('Developer registration failed: ' + err.error);
        }
      });
    }
  }
  
  
}
