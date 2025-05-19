import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { LoaderComponent } from '../../shared/components/loader/loader.component';

@Component({
  selector: 'app-oauth2-callback',
  standalone: true,
  imports: [LoaderComponent],
  template: `
    <div class="callback-container">
      <app-loader></app-loader>
      <p>Processing login...</p>
    </div>
  `,
  styles: [`
    .callback-container {
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      height: 100vh;
      background-color: #1a1a1a;
      color: var(--green-1, #00FF00);
    }
  `]
})
export class OAuth2CallbackComponent implements OnInit {
  
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}
  
  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const token = params['token'];
      const error = params['error'];
      
      // Get additional user info from URL params
      const userId = params['id'];
      const role = params['role'];
      const email = params['email'];
      const username = params['username'];
      
      if (error) {
        console.error('OAuth2 error:', error);
        this.router.navigate(['/login'], { 
          queryParams: { error: 'OAuth2 authentication failed' }
        });
        return;
      }
      
      if (token) {
        // Create user object with info from URL
        const userInfo = {
          id: userId ? parseInt(userId) : null,
          username: username || email,
          email: email,
          role: role
        };
        
        this.authService.handleOAuth2CallbackWithInfo(token, userInfo).subscribe({
          next: () => {
            console.log('OAuth2 login successful, user role:', role);
            this.router.navigate(['/dashboard']);
          },
          error: (err) => {
            console.error('OAuth2 callback error:', err);
            this.router.navigate(['/login'], { 
              queryParams: { error: 'Authentication failed' }
            });
          }
        });
      } else {
        this.router.navigate(['/login']);
      }
    });
  }
}