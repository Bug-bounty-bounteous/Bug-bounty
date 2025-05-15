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
import { TokenStorageService } from '../../../core/auth/token.storage';
import { User } from '../../../core/models/user.model';
import { Bug } from '../../../core/models/bug.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    SidebarLayoutComponent,
    LoaderComponent,
    ButtonComponent,
    DateFormatPipe,
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit {
  user: User;
  claimedBugs: Bug[] = [];
  uploadedBugs: Bug[] = [];
  isLoadingClaims: boolean = false;
  isLoadingUploadedBugs: boolean = false;
  isDeveloper: boolean = false;
  isCompany: boolean = false;
  techStacks: { name: string; count: number }[] = [];

  constructor(
    private userService: UserService,
    private bugService: BugService,
    private solutionService: SolutionService,
    private router: Router,
    private tokenStorage: TokenStorageService
  ) {}

  ngOnInit(): void {
    this.loadUserProfile();

    // Check user role
    const userRole = this.tokenStorage.getUserRole();
    this.isDeveloper = userRole === 'DEVELOPER';
    this.isCompany = userRole === 'COMPANY';

    // Only load claimed bugs for developers
    if (this.isDeveloper) {
      this.loadClaimedBugs();
    }

    // Only load uploaded bugs for company
    if (this.isCompany) {
      this.loadUploadedBugs();
    }
  }

  loadUserProfile(): void {
    this.userService.getCurrentUserProfile().subscribe({
      next: (userData) => {
        this.user = userData;
        // console.log('User Data');
        // console.log(this.user);
      },
      error: (error) => {
        console.error('Error loading user profile', error);
      },
    });
  }

  loadClaimedBugs(): void {
    this.isLoadingClaims = true;
    this.userService.getClaimedBugs().subscribe({
      next: (response) => {
        this.claimedBugs = response;
        this.isLoadingClaims = false;
        // console.log('Bugs');
        // console.log(this.claimedBugs);
        this.updateTechStackList();
      },
      error: (error) => {
        console.error('Error loading claimed bugs', error);
        this.isLoadingClaims = false;
      },
    });
  }

  loadUploadedBugs(): void {
    this.isLoadingUploadedBugs = true;
    this.userService.getUploadedBugs().subscribe({
      next: (response) => {
        this.uploadedBugs = response;
        this.isLoadingUploadedBugs = false;
      },
      error: (error) => {
        console.error('Error loading uploaded bugs', error);
        this.isLoadingUploadedBugs = false;
      },
    });
  }

  navigateToMarketplace(): void {
    this.router.navigate(['/marketplace']);
  }

  navigateToBugDetail(bugId: number): void {
    this.router.navigate(['/bugs', bugId]);
  }

  updateTechStackList(): void {
    if (this.claimedBugs.length <= 0) return;
    const counts = this.claimedBugs.reduce((acc, bug) => {
      bug.techStacks.forEach((tech) => {
        acc[tech.name] = (acc[tech.name] || 0) + 1;
      });
      return acc;
    }, {} as Record<string, number>);

    this.techStacks = Object.entries(counts).map(([name, count]) => ({
      name,
      count,
    }));
  }
}
