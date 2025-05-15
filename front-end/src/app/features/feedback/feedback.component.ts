import { Component } from '@angular/core';
import { OnInit } from '@angular/core';
import { SolutionService } from '../../core/services/solution.service';
import { FeedbackService } from '../../core/services/feedback.service';
import { TokenStorageService } from '../../core/auth/token.storage';
import { CommonModule } from '@angular/common'; 
import { FormsModule } from '@angular/forms';
import { AppComponent } from '../../app.component';

@Component({
  selector: 'app-feedback',
  standalone: true,
  imports: [CommonModule, FormsModule,],
  templateUrl: './feedback.component.html',
  styleUrl: './feedback.component.css'
})
export class FeedbackComponent implements OnInit {
  solutions: any[] = [];
  // developerId!: number;
  userId!: number;
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
      this.userId = user?.id;
      this.role = user?.role;

        if (this.role === 'COMPANY') {
      this.solutionService.getSolutionsByCompany(this.userId).subscribe({
        next: (data) => {
          console.log('Solutions fetched for company:', data);
          this.solutions = data;
        },
        error: (err) => console.error('Error fetching solutions for company', err)
      });
    } else if (this.role === 'DEVELOPER') {
      this.solutionService.getSolutionsByDeveloper(this.userId).subscribe({
        next: (data) => this.solutions = data,
        error: (err) => console.error('Error fetching solutions for developer', err)
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
        alert('Feedback submitted successfully!');
        this.isSubmitting = false;
        this.feedbackMessage = '';
        this.rating = 0;
        this.selectedSolutionId = 0;
      },
      error: (err) => {
        console.error('Error submitting feedback', err);
        this.isSubmitting = false;
      }
    });
  }
}
