import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { BugService } from '../../../core/services/bug.service';
import { Bug } from '../../../core/models/bug.model';
import { CardComponent } from '../../../shared/components/card/card.component';
import { LoaderComponent } from '../../../shared/components/loader/loader.component';
import { ButtonComponent } from '../../../shared/components/button/button.component';
import { DateFormatPipe } from '../../../shared/pipes/date-format.pipe';
import { TokenStorageService } from '../../../core/auth/token.storage';
import { SidebarLayoutComponent } from '../../../layout/sidebar-layout/sidebar-layout.component';

@Component({
  selector: 'app-bug-detail',
  standalone: true,
  imports: [
    CommonModule,
    CardComponent,
    LoaderComponent,
    ButtonComponent,
    DateFormatPipe,
    SidebarLayoutComponent
  ],
  templateUrl: './bug-detail.component.html',
  styleUrls: ['./bug-detail.component.css']
})
export class BugDetailComponent implements OnInit {
  bug: Bug | null = null;
  isLoading = false;
  errorMessage = '';
  currentUserId: number | undefined;
  
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private bugService: BugService,
    private tokenService: TokenStorageService
  ) {
    // Get current user ID
    const user = this.tokenService.getUser();
    this.currentUserId = user?.id;
  }

  ngOnInit(): void {
    this.loadBugDetails();
  }
  
  loadBugDetails(): void {
    this.isLoading = true;
    
    const bugId = this.route.snapshot.paramMap.get('id');
    if (!bugId) {
      this.errorMessage = 'Invalid bug ID';
      this.isLoading = false;
      return;
    }
    
    this.bugService.getBugById(Number(bugId)).subscribe({
      next: (bug) => {
        this.bug = bug;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading bug details', error);
        this.errorMessage = 'Failed to load bug details. Please try again later.';
        this.isLoading = false;
      }
    });
  }
  
  goBack(): void {
    this.router.navigate(['/bugs']);
  }
  
  claimBug(): void {
    // This will be implemented in a future requirement (Requirement 6)
    console.log('Claim bug functionality will be implemented in a future requirement');
  }
  
  getDifficultyClass(): string {
    if (!this.bug) return '';
    return 'difficulty-' + this.bug.difficulty.toLowerCase();
  }
  
  getStatusClass(): string {
    if (!this.bug) return '';
    return 'status-' + this.bug.status.toLowerCase();
  }
  
  isClaimable(): boolean {
    if (!this.bug) return false;
    return this.bug.status === 'OPEN';
  }
  
  isOwnedByCurrentUser(): boolean {
    if (!this.bug || !this.bug.publisher) return false;
    return this.bug.publisher.id === this.currentUserId;
  }

  submitSolution() {
    this.router.navigate(["/", "bugs", this.bug.id, "solutions", "create"]);
  }
}