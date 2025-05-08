import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { SidebarLayoutComponent } from '../../layout/sidebar-layout/sidebar-layout.component';
import { BugListComponent } from '../bugs/bug-list/bug-list.component';
import { ButtonComponent } from '../../shared/components/button/button.component';
import { TokenStorageService } from '../../core/auth/token.storage';

@Component({
  selector: 'app-marketplace',
  standalone: true,
  imports: [CommonModule, SidebarLayoutComponent, BugListComponent, ButtonComponent],
  templateUrl: './marketplace.component.html',
  styleUrls: ['./marketplace.component.css'],
})
export class MarketplaceComponent implements OnInit {
  isCompany: boolean = false;
  
  constructor(
    private router: Router,
    private tokenStorage: TokenStorageService
  ) {}
  
  ngOnInit(): void {
    // Check if user is a company
    const userRole = this.tokenStorage.getUserRole();
    this.isCompany = userRole === 'COMPANY';
  }
  
  navigateToBugCreate(): void {
    this.router.navigate(['/bugs/create']);
  }
}