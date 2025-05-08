import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InputBarComponent } from '../../shared/components/input-bar/input-bar.component';
import { ButtonComponent } from '../../shared/components/button/button.component';
import { AuthService } from '../../core/services/authservice';
import { Router } from '@angular/router';   

@Component({
  selector: 'app-unlock-captcha',
  imports: [CommonModule, FormsModule, InputBarComponent, ButtonComponent],
  templateUrl: './unlock-captcha.component.html',
  styleUrl: './unlock-captcha.component.css',
})
export class UnlockCaptchaComponent {
  email = '';
  captchaCode = '';
  message = '';


constructor(private router: Router, private authService: AuthService) {}

onSubmit(event: MouseEvent) {
  event.preventDefault();

  if (!this.email || !this.captchaCode) {
    this.message = 'Please enter both email and CAPTCHA code.';
    return;
  }

  this.authService.unlockAccount({
    email: this.email,
    captchaCode: this.captchaCode
  }).subscribe({
    next: (res) => {
      this.message = res;
      if (res.includes('unlocked')) {
        setTimeout(() => this.router.navigate(['/login']), 2000);
      }
    },
    error: (err) => {
      this.message = err.error;
    }
  });
}

  
}
