import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { NavBarComponent } from '../../layout/nav-bar/nav-bar.component';
import { ButtonComponent } from '../../shared/components/button/button.component';
import { AuthService } from '../../core/services/auth.service'; 
import { TokenStorageService } from '../../core/auth/token.storage';
import { AlertComponent } from '../../shared/components/alert/alert.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, NavBarComponent, ButtonComponent, AlertComponent],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  email: string = '';
  password: string = '';
  rememberMe: boolean = false;
  hasError: boolean = false;
  errorMessage: string = 'Invalid username or password';
  isSubmitting: boolean = false;
  isLocked: boolean = false;
  lockoutTimeRemaining: number = 0;
  lockoutTimer: any;
  returnUrl: string = '/dashboard';

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private authService: AuthService,
    private tokenStorage: TokenStorageService
  ) {}

  ngOnInit(): void {
    // Get return url from route parameters or default to '/dashboard'
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/dashboard';
    
    // Check if already logged in
    if (this.tokenStorage.isLoggedIn()) {
      this.router.navigate([this.returnUrl]);
      return;
    }
    
    // Check if account is locked
    this.checkLockStatus();
  }
  
  ngOnDestroy(): void {
    if (this.lockoutTimer) {
      clearInterval(this.lockoutTimer);
    }
  }

  OnEmailChange(value: string): void {
    this.email = value;
    this.hasError = false;
  }

  OnPasswordChange(value: string): void {
    this.password = value;
    this.hasError = false;
  }
  
  OnRememberMeChange(value: boolean): void {
    this.rememberMe = value;
  }

  OnClickLogin(event: MouseEvent): void {
    // Prevent multiple submissions
    if (this.isSubmitting) {
      return;
    }
  
    if (!this.email || !this.password) {
      this.errorMessage = 'Email and password are required';
      this.hasError = true;
      return;
    }
    
    // Check if account is locked before sending the request
    if (this.authService.isLockedOut()) {
      const remainingMinutes = Math.ceil(this.authService.getRemainingLockoutTime() / 60000);
      this.errorMessage = `Your account is temporarily locked. Please try again in ${remainingMinutes} minutes.`;
      this.hasError = true;
      return;
    }
    
    this.isSubmitting = true;
    this.hasError = false;
    
    // Create a local reference to prevent closure issues
    const loginSubscription = this.authService.login({
      email: this.email,
      password: this.password,
      rememberMe: this.rememberMe
    }).subscribe({
      next: (response) => {
        this.isSubmitting = false;
        loginSubscription.unsubscribe();
        this.router.navigate([this.returnUrl]);
      },
      error: (err) => {
        console.log('Login error:', err);
        // Always reset isSubmitting on error
        this.isSubmitting = false;
        loginSubscription.unsubscribe();
        
        this.hasError = true;
        
        if (err.status === 401) {
          this.errorMessage = 'Invalid email or password';
          this.authService.handleLoginFailure();
          
          // Check if this attempt has locked the account
          if (this.authService.isLockedOut()) {
            const remainingMinutes = Math.ceil(this.authService.getRemainingLockoutTime() / 60000);
            this.errorMessage = `Your account is temporarily locked. Please try again in ${remainingMinutes} minutes.`;
            this.isLocked = true;
          }
        } else if (err.status === 423) {
          this.errorMessage = err.error?.message || 'Your account is temporarily locked';
          this.isLocked = true;
        } else if (err.status === 0) {
          this.errorMessage = 'Cannot connect to server. Please check your internet connection.';
        } else {
          this.errorMessage = err.error?.message || 'An error occurred during login';
        }
      },
      complete: () => {
        // Ensure isSubmitting is always reset
        this.isSubmitting = false;
      }
    });
  }

  goToSignUp(event: MouseEvent): void {
    this.router.navigate(['/register']);
  }
  
  forgotPassword(event: MouseEvent): void {
    // Navigate to password reset page - will be implemented in a future requirement
    this.router.navigate(['/forgot-password']);
  }
  
  private checkLockStatus(): void {
    this.isLocked = this.authService.isLockedOut();
    
    if (this.isLocked) {
      this.lockoutTimeRemaining = this.authService.getRemainingLockoutTime();
      this.errorMessage = `Your account is temporarily locked. Please try again in ${Math.ceil(this.lockoutTimeRemaining / 60000)} minutes.`;
      this.hasError = true;
      
      // Start timer to update remaining time
      this.lockoutTimer = setInterval(() => {
        this.lockoutTimeRemaining = this.authService.getRemainingLockoutTime();
        
        if (this.lockoutTimeRemaining <= 0) {
          clearInterval(this.lockoutTimer);
          this.isLocked = false;
          this.hasError = false;
        } else {
          this.errorMessage = `Your account is temporarily locked. Please try again in ${Math.ceil(this.lockoutTimeRemaining / 60000)} minutes.`;
        }
      }, 30000); // Update every 30 seconds
    }
  }
}