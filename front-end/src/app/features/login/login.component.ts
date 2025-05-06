import { Component } from '@angular/core';
import { NavBarComponent } from '../../layout/nav-bar/nav-bar.component';
import { CommonModule } from '@angular/common';
import { InputBarComponent } from '../../shared/components/input-bar/input-bar.component';
import { ButtonComponent } from '../../shared/components/button/button.component';

@Component({
  selector: 'app-login',
  imports: [NavBarComponent, CommonModule, InputBarComponent, ButtonComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  username: string = '';
  password: string = '';

  hasError: boolean = false;

  OnUsernameChange(value: string) {
    this.username = value;
  }

  OnPasswordChange(value: string) {
    this.password = value;
  }
}
