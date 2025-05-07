import { Component } from '@angular/core';
import { NavBarComponent } from '../../layout/nav-bar/nav-bar.component';
import { CommonModule } from '@angular/common';
import { InputBarComponent } from '../../shared/components/input-bar/input-bar.component';
import { FormsModule } from '@angular/forms';
import { ButtonComponent } from '../../shared/components/button/button.component';

@Component({
  selector: 'app-sign-up',
  standalone: true,
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
  isCompany: boolean = false;

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
    console.log('Submit Sign Up form logic here...');
  }
}
