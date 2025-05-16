import { Component, Input } from '@angular/core';
import { FeedbackService } from '../../core/services/feedback.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-feedback',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './feedback.component.html',
  styleUrls: ['./feedback.component.css']
})
export class FeedbackComponent {
  @Input() solutionId!: number;  

  feedbackMessage: string = '';
  rating: number = 0;
  isSubmitting: boolean = false;

  constructor(private feedbackService: FeedbackService) {}

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
        alert('Feedback submitted successfully!');
        this.isSubmitting = false;
        this.feedbackMessage = '';
        this.rating = 0;
      },
      error: (err) => {
        console.error('Error submitting feedback', err);
        this.isSubmitting = false;
      }
    });
  }
}