import { Routes } from '@angular/router';
import { AuthGuard } from './core/auth/auth.guard';
import { RoleGuard } from './core/auth/role.guard';
import { OAuth2CallbackComponent } from './features/auth/oauth2-callback.component';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./features/home/home.component').then((m) => m.HomeComponent),
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./features/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/sign-up/sign-up.component').then(
        (m) => m.SignUpComponent
      ),
  },
  {
    path: 'marketplace',
    loadComponent: () =>
      import('./features/marketplace/marketplace.component').then(
        (m) => m.MarketplaceComponent
      ),
    canActivate: [AuthGuard],
  },
  {
    path: 'bugs/create',
    loadComponent: () =>
      import('./features/bugs/bug-create/bug-create.component').then(
        (m) => m.BugCreateComponent
      ),
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['COMPANY'] },
  },
  {
    path: 'bugs/:id',
    loadComponent: () =>
      import('./features/bugs/bug-detail/bug-detail.component').then(
        (m) => m.BugDetailComponent
      ),
    canActivate: [AuthGuard],
  },
  {
    path: 'bugs/:id/solutions/create',
    loadComponent: () => import('./features/solutions/solution-create/solution-create.component').then(m => m.SolutionCreateComponent),
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['DEVELOPER'] }
  },
  {
    path: 'bugs/:id/solutions',
    loadComponent: () => import('./features/solutions/solution-list/solution-list.component').then(m => m.SolutionListComponent),
    canActivate: [AuthGuard],
  },
  {
    path: 'solutions/:id',
    loadComponent: () => import('./features/solutions/solution-review/solution-review.component').then(m => m.SolutionReviewComponent),
    canActivate: [AuthGuard],
  },
  {
    path: 'dashboard',
    loadComponent: () =>
      import('./features/user/dashboard/dashboard.component').then(
        (m) => m.DashboardComponent
      ),
    canActivate: [AuthGuard],
  },
  {
    path: 'resources',
    loadComponent: () => import('./features/resources/resource-list/resource-list.component').then(m => m.ResourceListComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'resources/create',
    loadComponent: () => import('./features/resources/resource-create/resource-create.component').then(m => m.ResourceCreateComponent),
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['COMPANY'] }
  },
  {
    path: 'about',
    loadComponent: () =>
      import('./features/about/about.component').then((m) => m.AboutComponent),
  },
  {
    path: 'leaderboard',
    loadComponent: () =>
      import('./features/leaderboard/leaderboard.component').then(
        (m) => m.LeaderboardComponent
      ),
  },
  {
    path: 'oauth2/callback',
    component: OAuth2CallbackComponent
  },
  {
  path: 'my-feedback',
  loadComponent: () => import('./features/feedback/feedback.component').then(m => m.FeedbackComponent)
  },
  {
    path: '**',
    redirectTo: '',
  },
];
