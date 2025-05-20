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
import { Bug } from '../../../core/models/bug.model';

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
  isSolutionPublisher: boolean = false;
  user: User;
  isSendingAccept: boolean = false;
  isSendingRefuse: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';
  feedbacks: Feedback[] = [];
  bug: Bug;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private solutionService: SolutionService,
    private userService: UserService,
    private feedbackService: FeedbackService,
    private tokenService: TokenStorageService,
    private bugService: BugService
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
        this.loadBug();
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = error.error?.message || error.error?.error?.message || "Failed to load solution";
      }
    })
  }

  loadBug() {
    this.bugService.getBugById(this.solution.bug.id).subscribe({
      next: (bug) => this.bug = bug,
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = error.error?.message || error.error?.error?.message || "Failed to load bug";
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
    this.isSolutionPublisher = false;
    this.isPublisher = false;
    if (this.user.role == 'DEVELOPER') {
      this.solutionService.getSolutionsByDeveloper(this.user.id).subscribe(
        {
          next: (solutions) => {
            for (let solution of solutions) {
              if (solution.id == this.solution.id) {
                this.isSolutionPublisher = true;
                return;
              }
              this.isSolutionPublisher = false;
            }
          },
          error: (error) => {
            this.isSolutionPublisher = false;
            this.errorMessage = error.error?.message || error.error?.error?.message || "You are not the claimer for this bug";
          }
        }

      );
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
    this.router.navigate(["/", "solutions", this.solution.id, "feedback", "create"]);
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

