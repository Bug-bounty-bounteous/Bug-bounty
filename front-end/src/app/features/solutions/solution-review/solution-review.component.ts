import { Component, OnInit } from '@angular/core';
import { SidebarLayoutComponent } from "../../../layout/sidebar-layout/sidebar-layout.component";
import { LoaderComponent } from "../../../shared/components/loader/loader.component";
import { ButtonComponent } from "../../../shared/components/button/button.component";
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { SolutionService } from '../../../core/services/solution.service';
import { UserService } from '../../../core/services/user.service';
import { Solution } from '../../../core/models/solution.model';
import { Bug } from '../../../core/models/bug.model';
import { BugService } from '../../../core/services/bug.service';
import { User } from '../../../core/models/user.model';
import { TokenStorageService } from '../../../core/auth/token.storage';
import { AlertComponent } from "../../../shared/components/alert/alert.component";
import { Feedback } from '../../../core/models/feedback.model';
import { FeedbackService } from '../../../core/services/feedback.service';

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
  allowedToView: boolean = false;
  solution: Solution;
  bug: Bug;
  isDownloading: boolean = false;
  isPublisher: boolean = false;
  isClaimer: boolean = false;
  canAct: boolean = false;
  user: User;
  isSendingAccept: boolean = false;
  isSendingRefuse: boolean = false;
  isSendingFeedback: boolean = false;
  errorMessage: string = '';
  successMessage: string;
  feedbacks: Feedback[];

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private bugService: BugService,
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

  loadFeedbacks() {
    this.isLoadingFeedbacks = true;
    this.feedbackService.getFeedbacksForSolution(this.solution.id).subscribe(
        {
            next: (feedbacks) => {
                this.feedbacks = feedbacks;
                this.isLoadingFeedbacks = false;
            },
            error: (error) => {
                this.errorMessage = error;
                this.isLoadingFeedbacks = false;
            }
        }
    );
  }

  loadSolutionReview() {
    let solutionId = Number(this.route.snapshot.paramMap.get('id'));
    this.solutionService.getSolutionById(solutionId).subscribe({
      next: (response) => {
        this.solution = response;
        this.bug = this.solution.bug;
        this.checkAllowedToViewOrAct();
        this.loadFeedbacks();
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = error;
      }
    })
  }

  downloadSolutionFile() {
    this.isDownloading = true;
    this.solutionService.downloadSolutionFile(this.solution.id).subscribe({
      next: (data) => {
          const blob = new Blob([data]);
          const url= window.URL.createObjectURL(blob);
          window.open(url);
          this.isDownloading = false;
      },
      error: (error) => {
        this.errorMessage = error;
        this.isDownloading = false;
      }
    })
  }
  checkAllowedToViewOrAct() {
    if (this.user.role == 'DEVELOPER') {
      this.userService.getClaimedBugs().subscribe(
        {
          next: (bugs) => {
            for (let bug of bugs) {
              if (bug.id == this.bug.id) {
                this.isClaimer = true;
                return;
              }
              this.isClaimer = false;
            }
          },
          error: (error) => {
            this.isClaimer = false;
            this.errorMessage = error;
          }
        }
      )
    } else {
      this.userService.getUploadedBugs().subscribe(
        {
          next: (bugs) => {
            for (let bug of bugs) {
              if (bug.id == this.bug.id) {
                this.isPublisher = true;
                this.checkAllowedToAct();
                return;
              }
              this.isPublisher = false;
            }
          },
          error: (error) => {
            this.isPublisher = false;
            this.errorMessage = error;
          }
        }
      )
    }
    this.isLoading = false;
  }

  checkAllowedToAct() {
    this.canAct = this.solution.status == "SUBMITTED";
  }

  sendAcceptSolution() {
    this.isSendingAccept = true;
    this.solutionService.acceptSolution(this.solution.id).subscribe({
      next: (res) => {
        this.successMessage = "Solution accepted successfully!";
        this.isSendingAccept = false;
      }, 
      error: (error) => {
        this.errorMessage = error;
        this.isSendingAccept = false;
      }
    });
  }
}

