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
import { UserService } from '../../../core/services/user.service';
import { catchError, map, of } from 'rxjs';

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
export class BugDetailComponent implements OnInit{
  bug: Bug | null = null;
  isLoading = false;
  errorMessage = '';
  currentUserId: number | undefined;
  isClaimingBug = false;
  successMessage = '';
  userRole: string | null = null;
  isClaimedByYou = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private bugService: BugService,
    private userService: UserService,
    private tokenService: TokenStorageService
  ) {
    // Get current user ID
    const user = this.tokenService.getUser();
    this.currentUserId = user?.id;
    this.userRole = this.tokenService.getUserRole();
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
    this.checkIfClaimedByUser();
  }

  goBack(): void {
    this.router.navigate(['/marketplace']);
  }

  claimBug(): void {
    if (!this.isClaimable() || this.isOwnedByCurrentUser()) {
      return;
    }

    this.isClaimingBug = true;

    this.bugService.claimBug(this.bug.id).subscribe({
      next: (response) => {
        this.isClaimingBug = false;
        this.successMessage = "Bug claimed successfully! You can now start working on it.";

        // Reload the bug to update the status
        setTimeout(() => {
          this.loadBugDetails();
        }, 2000);
      },
      error: (error) => {
        this.isClaimingBug = false;
        console.error('Error claiming bug', error);
        this.errorMessage = error.error?.message || 'Failed to claim bug. Please try again later.';
      }
    });
  }

  unclaimBug() {
    this.checkIfClaimedByUser();
    this.isClaimingBug = true;
    if (this.isClaimedByYou) {
      this.bugService.unclaimBug(this.bug.id).subscribe(
        {
          next: () => {
            this.successMessage = "Bug unclaimed successfully!"
            this.isClaimingBug = false;
            setTimeout(() => {
              this.loadBugDetails();
            }, 2000);
          },
          error: (error) => {
            this.isClaimingBug = false;
            console.error('Error unclaiming bug', error);
            this.errorMessage = error.error?.message || 'Failed to unclaim bug. Please try again later.';
          }
        }
      );
    } else {
      this.errorMessage = "This is not a bug you claimed."
      this.isClaimingBug = false;
    }

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
    
    // Check if user is a developer (companies cannot claim bugs)
    const userRole = this.tokenService.getUserRole();
    const isDeveloper = userRole === 'DEVELOPER';
    
    return this.bug.status === 'OPEN' && isDeveloper;
  }


  checkIfClaimedByUser(): boolean {
    if (this.userRole == "DEVELOPER") {
      this.userService.getClaimedBugs().subscribe({
        next: (bugs) => {
          for (let bug of bugs) {
            if (bug.id == this.bug.id) {
              this.isClaimedByYou = true;
              return;
            }
          }
          this.isClaimedByYou = false;
        },
        error: (error) => {
          this.isClaimedByYou = false;
        }
      });
    }
    return true
  }
  isOwnedByCurrentUser(): boolean {
    if (!this.bug || !this.bug.publisher) return false;
    return this.bug.publisher.id === this.currentUserId;
  }

  submitSolution() {
    this.router.navigate(["/", "bugs", this.bug.id, "solutions", "create"]);
  }
}