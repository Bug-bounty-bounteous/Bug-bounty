import { Component } from '@angular/core';
import { NavBarComponent } from '../../layout/nav-bar/nav-bar.component';
import { CommonModule } from '@angular/common';
import { InputBarComponent } from '../../shared/components/input-bar/input-bar.component';
import { ButtonComponent } from '../../shared/components/button/button.component';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/authservice';


@Component({
  selector: 'app-login',
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

  constructor(private router: Router, private authService: AuthService) {}


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
    event.preventDefault();
  
    if (!this.username || !this.password) {
      this.hasError = true;
      return;
    }
  
    const payload = {
      email: this.username,
      plainPassword: this.password
    };
  

    this.authService.loginDeveloper(payload).subscribe({
      next: (res) => {
        this.authService.storeToken(res.token);
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        const message = err.error;
  
        if (message.includes('locked')) {
          alert('Your account is locked. Redirecting to CAPTCHA page...');
          this.router.navigate(['/unlock']);
          return;
        }
  

        this.authService.loginCompany(payload).subscribe({
          next: (res) => {
            this.authService.storeToken(res.token);
            this.router.navigate(['/dashboard']);
          },
          error: (err) => {
            const message = err.error;
  
            if (message.includes('locked')) {
              alert('Your account is locked. Redirecting to CAPTCHA page...');
              this.router.navigate(['/unlock']);
            } else {
              this.hasError = true;
              alert(message);
            }
          }
        });
      }
    });
  }
  

}
