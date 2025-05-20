import { Component, Input, OnInit } from '@angular/core';
import { FeedbackService } from '../../core/services/feedback.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AlertComponent } from '../../shared/components/alert/alert.component';

@Component({
  selector: 'app-feedback',
  standalone: true,
  imports: [CommonModule, FormsModule, AlertComponent],
  templateUrl: './feedback.component.html',
  styleUrls: ['./feedback.component.css']
})
export class FeedbackComponent implements OnInit{
  solutionId!: number;  

  feedbackMessage: string = '';
  rating: number = 0;
  isSubmitting: boolean = false;
  successMessage: string = '';

  constructor(
    private feedbackService: FeedbackService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.solutionId = Number(this.route.snapshot.paramMap.get("solutionId"))
  }
  ngOnInit(): void {
    this.feedbackMessage = '';
    this.rating = 0;
  }


  submitFeedback(): void {
    if (!this.solutionId || !this.feedbackMessage || !this.rating) {
      console.error('All fields are required');
      return;
    }

    this.isSubmitting = true;

    this.feedbackService.submitFeedback({
      solutionId: this.solutionId,
      feedbackMessage: this.feedbackMessage,
      rating: this.rating
    }).subscribe({
      next: () => {
        this.successMessage = 'Feedback submitted successfully!';
        this.isSubmitting = false;
        setTimeout(
          () => {
            this.router.navigate(["/", "solutions", this.solutionId])
          }
          , 1000)
      },
      error: (err) => {
        console.error('Error submitting feedback', err);
        this.isSubmitting = false;
      }
    });
  }
}