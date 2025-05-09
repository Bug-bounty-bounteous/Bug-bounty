import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router'; 
import { UserService } from '../../../core/services/user.service';
import { BugService } from '../../../core/services/bug.service';
import { SolutionService } from '../../../core/services/solution.service';
import { SidebarLayoutComponent } from '../../../layout/sidebar-layout/sidebar-layout.component';
import { LoaderComponent } from '../../../shared/components/loader/loader.component';
import { ButtonComponent } from '../../../shared/components/button/button.component';
import { DateFormatPipe } from '../../../shared/pipes/date-format.pipe';

@Component({
  selector: 'app-dashboard',
  standalone: true, 
  imports: [
    CommonModule, 
    SidebarLayoutComponent, 
    LoaderComponent, 
    ButtonComponent,
    DateFormatPipe
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  user: any; 
  claimedBugs: any[] = [];
  isLoadingClaims = false;
  
  constructor(
    private userService: UserService,
    private bugService: BugService,
    private solutionService: SolutionService,
    private router: Router 
  ) { }
  
  ngOnInit(): void {
    this.loadUserProfile();
    this.loadClaimedBugs();
  }
  
  loadUserProfile(): void {
    this.userService.getCurrentUserProfile().subscribe({
      next: (userData) => {
        this.user = userData;
      },
      error: (error) => {
        console.error('Error loading user profile', error);
      }
    });
  }
  
  loadClaimedBugs(): void {
    this.isLoadingClaims = true;
    this.userService.getClaimedBugs().subscribe({
      next: (response) => {
        this.claimedBugs = response;
        this.isLoadingClaims = false;
      },
      error: (error) => {
        console.error('Error loading claimed bugs', error);
        this.isLoadingClaims = false;
      }
    });
  }
  
  navigateToMarketplace(): void {
    this.router.navigate(['/marketplace']);
  }
  
  navigateToBugDetail(bugId: number): void {  
    this.router.navigate(['/bugs', bugId]);
  }
}