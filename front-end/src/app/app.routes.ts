import { Routes } from '@angular/router';
import { AuthGuard } from './core/auth/auth.guard';
import { RoleGuard } from './core/auth/role.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./features/home/home.component').then(m => m.HomeComponent)
  },
  {
    path: 'login',
    loadComponent: () => import('./features/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./features/sign-up/sign-up.component').then(m => m.SignUpComponent)
  },
  {
    path: 'marketplace',
    loadComponent: () => import('./features/marketplace/marketplace.component').then(m => m.MarketplaceComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'bugs',
    loadComponent: () => import('./features/bugs/bug-list/bug-list.component').then(m => m.BugListComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'bugs/create',
    loadComponent: () => import('./features/bugs/bug-create/bug-create.component').then(m => m.BugCreateComponent),
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['COMPANY'] }
  },
  {
    path: 'bugs/:id',
    loadComponent: () => import('./features/bugs/bug-detail/bug-detail.component').then(m => m.BugDetailComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./features/user/dashboard/dashboard.component').then(m => m.DashboardComponent),
    canActivate: [AuthGuard]
  },
  {
    path: 'about',
    loadComponent: () => import('./features/about/about.component').then(m => m.AboutComponent)
  },
  {
    path: '**',
    redirectTo: ''
  }
];