import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home.component';
import { AboutComponent } from './features/about/about.component';
import { LoginComponent } from './features/login/login.component';
import { SignUpComponent } from './features/sign-up/sign-up.component';
import { MarketplaceComponent } from './features/marketplace/marketplace.component';

export const routes: Routes = [
  { path: '', title: 'Home | Bug Bounty', component: HomeComponent },
  { path: 'about', title: 'About | Bug Bounty', component: AboutComponent },
  { path: 'login', title: 'Login | Bug Bounty', component: LoginComponent },
  { path: 'signup', title: 'Sign Up | Bug Bounty', component: SignUpComponent },
  {
    path: 'marketplace',
    title: 'Bug Marketplace | Bug Bounty',
    component: MarketplaceComponent,
  },
];
