import { Component } from '@angular/core';
import { OnInit } from '@angular/core';
import { SolutionService } from '../../core/services/solution.service';
import { FeedbackService } from '../../core/services/feedback.service';
import { TokenStorageService } from '../../core/auth/token.storage';
import { CommonModule } from '@angular/common'; 
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-feedback',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './feedback.component.html',
  styleUrl: './feedback.component.css'
})
export class FeedbackComponent implements OnInit {
  solutions: any[] = [];
  developerId!: number;
  role: string = '';
  
  // For Company Feedback Form
  selectedSolutionId!: number;
  feedbackMessage: string = '';
  rating: number = 0;
  isSubmitting: boolean = false;

  constructor(
    private solutionService: SolutionService,
    private feedbackService: FeedbackService,
    private tokenStorage: TokenStorageService
  ) {}

  ngOnInit(): void {
    const user = this.tokenStorage.getUser();
    this.developerId = user?.id;
    this.role = user?.role;

    if (this.role === 'DEVELOPER' && this.developerId) {
      this.solutionService.getSolutionsByDeveloper(this.developerId).subscribe({
        next: (data) => this.solutions = data,
        error: (err) => console.error('Error fetching solutions', err)
      });
    }

    if (this.role === 'COMPANY') {
      this.solutionService.getSolutionsByCompany().subscribe({
        next: (data) => this.solutions = data,
        error: (err) => console.error('Error fetching solutions', err)
      });
    }
  }

  submitFeedback(): void {
    if (!this.selectedSolutionId || !this.feedbackMessage || !this.rating) return;

    this.isSubmitting = true;

    this.feedbackService.submitFeedback({
      solutionId: this.selectedSolutionId,
      feedbackMessage: this.feedbackMessage,
      rating: this.rating
    }).subscribe({
      next: () => {
        alert('Feedback submitted!');
        this.isSubmitting = false;
        this.feedbackMessage = '';
        this.rating = 0;
        this.selectedSolutionId = 0;
      },
      error: (err) => {
        console.error('Feedback submission error', err);
        this.isSubmitting = false;
      }
    });
  }
}
