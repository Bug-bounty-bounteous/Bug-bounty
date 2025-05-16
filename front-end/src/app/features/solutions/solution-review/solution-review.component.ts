import { Component, OnInit } from '@angular/core';
import { SidebarLayoutComponent } from "../../../layout/sidebar-layout/sidebar-layout.component";
import { LoaderComponent } from "../../../shared/components/loader/loader.component";
import { ButtonComponent } from "../../../shared/components/button/button.component";
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { SolutionService } from '../../../core/services/solution.service';
import { UserService } from '../../../core/services/user.service';
import { Solution } from '../../../core/models/solution.model';
import { BugService } from '../../../core/services/bug.service';
import { User } from '../../../core/models/user.model';
import { TokenStorageService } from '../../../core/auth/token.storage';
import { AlertComponent } from "../../../shared/components/alert/alert.component";
import { Feedback } from '../../../core/models/feedback.model';
import { FeedbackService } from '../../../core/services/feedback.service';
import { HttpErrorResponse } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-solution-review',
  standalone: true,
  imports: [
    CommonModule,
    SidebarLayoutComponent,
    LoaderComponent,
    ButtonComponent,
    AlertComponent
],
  templateUrl: './solution-review.component.html',
  styleUrl: './solution-review.component.css'
})
export class SolutionReviewComponent implements OnInit {
  isLoading: boolean = true;
  isLoadingFeedbacks: boolean = true;
  solution: Solution;
  isDownloading: boolean = false;
  isPublisher: boolean = false;
  isClaimer: boolean = false;
  user: User;
  isSendingAccept: boolean = false;
  isSendingRefuse: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';
  feedbacks: Feedback[] = [];

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private solutionService: SolutionService,
    private userService: UserService,
    private feedbackService: FeedbackService,
    private tokenService: TokenStorageService
  ) {
    this.user = this.tokenService.getUser();
  }

  ngOnInit(): void {
    this.loadSolutionReview();
  }


  loadSolutionReview() {
    this.isLoading = true;
    let solutionId = Number(this.route.snapshot.paramMap.get('id'));
    this.solutionService.getSolutionById(solutionId).subscribe({
      next: (response) => {
        this.solution = response;
        this.checkAllowedToViewOrAct();
        this.loadFeedbacks();
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = error.error?.message || error.error?.error?.message || "Failed to load solution";
      }
    })
  }

  loadFeedbacks() {
    this.isLoadingFeedbacks = true;
    this.feedbackService.getFeedbacksForSolution(this.solution.id).subscribe(
        {
            next: (feedbacks) => {
                this.feedbacks = feedbacks;
                this.isLoadingFeedbacks = false;
            },
            error: (error) => {
                this.errorMessage = error.error?.message || error.error?.error?.message ||  "Failed to load feedbacks";
                this.isLoadingFeedbacks = false;
            }
        }
    );
  }

  downloadSolutionFile() {
    this.solutionService.downloadSolutionFile(this.solution.id);
  }
  
  checkAllowedToViewOrAct() {
    this.isClaimer = false;
    this.isPublisher = false;
    if (this.user.role == 'DEVELOPER') {
      this.userService.getClaimedBugs().subscribe(
        {
          next: (bugs) => {
            for (let bug of bugs) {
              if (bug.id == this.solution.bug.id) {
                this.isClaimer = true;
                return;
              }
              this.isClaimer = false;
            }
          },
          error: (error) => {
            this.isClaimer = false;
            this.errorMessage = error.error?.message || error.error?.error?.message || "You are not the claimer for this bug";
          }
        }
      )
    } else {
      this.userService.getUploadedBugs().subscribe(
        {
          next: (bugs) => {
            for (let bug of bugs) {
              if (bug.id == this.solution.bug.id) {
                this.isPublisher = true;
                return;
              }
              this.isPublisher = false;
            }
          },
          error: (error) => {
            this.isPublisher = false;
            this.errorMessage = error.error?.message || error.error?.error?.message || "You are not the publisher for this bug";
          }
        }
      )
    }
    this.isLoading = false;
  }


  sendAcceptSolution() {
    this.isSendingAccept = true;
    this.solutionService.acceptSolution(this.solution.id).subscribe({
      next: (res) => {
        this.successMessage = "Solution accepted successfully!";
        this.isSendingAccept = false;
        this.loadSolutionReview();
      }, 
      error: (error) => {
        this.errorMessage = error.error?.message || error.error?.error?.message || "Solution failed to be accepted";
        this.isSendingAccept = false;
      }
    });
  }

  sendFeedback() {
    this.router.navigate(["/", "my-feedback", this.solution.id]);
  }

  sendRefuseSolution() {
    this.isSendingRefuse = true;
    this.solutionService.refuseSolution(this.solution.id).subscribe({
      next: (res) => {
        this.successMessage = "Solution rejected successfully!";
        this.isSendingRefuse = false;
        this.loadSolutionReview();
      }, 
      error: (error) => {
        this.errorMessage = error.error?.message || error.error?.error?.message || "Solution failed to be rejected";
        this.isSendingRefuse = false;
      }
    });
  }
}

