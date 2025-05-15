import { Component, OnInit, OnDestroy } from '@angular/core';
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
export class LoginComponent implements OnInit, OnDestroy {
  email: string = '';
  password: string = '';
  errorMessage: string = '';
  hasError: boolean = false;
  isSubmitting: boolean = false;
  isLocked: boolean = false;
  showCaptcha: boolean = false;
  captchaCode: string = '';
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
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/dashboard';

    if (this.tokenStorage.isLoggedIn()) {
      this.router.navigate([this.returnUrl]);
      return;
    }

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

  OnClickLogin(event: MouseEvent): void {
    if (this.isSubmitting) return;

    if (!this.email || !this.password) {
      this.errorMessage = 'Email and password are required';
      this.hasError = true;
      return;
    }

    this.isSubmitting = true;
    this.hasError = false;

    const loginSub = this.authService.login({
      email: this.email,
      password: this.password,
    }).subscribe({
      next: (res) => {
        this.isSubmitting = false;
        loginSub.unsubscribe();
        this.router.navigate([this.returnUrl]);
      },
      error: (err) => {
        this.isSubmitting = false;
        loginSub.unsubscribe();
        this.hasError = true;

        const msg = err?.error?.message?.toLowerCase() || '';

        if (msg.includes('account locked')) {
          this.errorMessage = 'Account locked. Please enter CAPTCHA to unlock.';
          this.isLocked = true;
          this.showCaptcha = true;

        } else if (msg.includes('wrong password')) {
          this.errorMessage = err.error.message;

        } else if (msg.includes('invalid email')) {
          this.errorMessage = 'Invalid email address';

        } else if (err.status === 0) {
          this.errorMessage = 'Cannot connect to server. Please check your internet connection.';

        } else {
          this.errorMessage = 'Login failed. Please try again.';
        }
      }
    });
  }

  onUnlockClick(): void {
    if (!this.email || !this.captchaCode) {
      this.errorMessage = 'Email and CAPTCHA code are required.';
      this.hasError = true;
      return;
    }

    this.authService.unlockAccount(this.email, this.captchaCode).subscribe({
      next: () => {
        this.errorMessage = 'Account unlocked. You can now log in.';
        this.hasError = true;
        this.isLocked = false;
        this.showCaptcha = false;
        this.captchaCode = '';
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to unlock account.';
        this.hasError = true;
      }
    });
  }

  goToSignUp(event: MouseEvent): void {
    this.router.navigate(['/register']);
  }

  forgotPassword(event: MouseEvent): void {
    this.router.navigate(['/forgot-password']);
  }

  onGoogleLogin(): void {
    this.authService.initGoogleLogin();
  }

  private checkLockStatus(): void {
    this.isLocked = this.authService.isLockedOut?.() || false;

    if (this.isLocked) {
      this.lockoutTimeRemaining = this.authService.getRemainingLockoutTime?.() || 0;
      this.errorMessage = `Your account is temporarily locked. Please try again later.`;
      this.hasError = true;

      this.lockoutTimer = setInterval(() => {
        this.lockoutTimeRemaining = this.authService.getRemainingLockoutTime();
        if (this.lockoutTimeRemaining <= 0) {
          clearInterval(this.lockoutTimer);
          this.isLocked = false;
          this.hasError = false;
        }
      }, 30000);
    }
  }
}