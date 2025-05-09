import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { BugService } from '../../../core/services/bug.service';
import { TokenStorageService } from '../../../core/auth/token.storage';
import { AlertComponent } from '../../../shared/components/alert/alert.component';
import { ButtonComponent } from '../../../shared/components/button/button.component';
import { CardComponent } from '../../../shared/components/card/card.component';
import { LoaderComponent } from '../../../shared/components/loader/loader.component';
import { SidebarLayoutComponent } from '../../../layout/sidebar-layout/sidebar-layout.component';

@Component({
  selector: 'app-bug-create',
  standalone: true,
  imports: [
    CommonModule, 
    ReactiveFormsModule, 
    AlertComponent, 
    ButtonComponent, 
    CardComponent, 
    LoaderComponent,
    SidebarLayoutComponent
  ],
  templateUrl: './bug-create.component.html',
  styleUrls: ['./bug-create.component.css']
})
export class BugCreateComponent implements OnInit {
  bugForm: FormGroup;
  difficulties: string[] = [];
  techStacks: any[] = [];
  selectedTechStacks: number[] = [];
  
  isLoading = false;
  isSubmitting = false;
  errorMessage = '';
  successMessage = '';
  isCompany = false;

  constructor(
    private fb: FormBuilder,
    private bugService: BugService,
    private router: Router,
    private tokenStorage: TokenStorageService
  ) {
    this.bugForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(100)]],
      description: ['', [Validators.required, Validators.minLength(20)]],
      difficulty: ['', Validators.required],
      reward: [0, [Validators.required, Validators.min(50)]]
    });
    
    // Check if user is a company
    const userRole = this.tokenStorage.getUserRole();
    this.isCompany = userRole === 'COMPANY';
  }

  ngOnInit(): void {
    if (!this.isCompany) {
      // Redirect if not a company
      this.router.navigate(['/marketplace']);
      return;
    }
    
    this.loadDifficulties();
    this.loadTechStacks();
  }
  
  loadDifficulties(): void {
    this.bugService.getDifficulties().subscribe({
      next: (difficulties) => {
        this.difficulties = difficulties;
      },
      error: (error) => {
        console.error('Error loading difficulties', error);
        this.errorMessage = 'Failed to load difficulty levels';
      }
    });
  }
  
  loadTechStacks(): void {
    this.bugService.getTechStacks().subscribe({
      next: (techStacks) => {
        this.techStacks = techStacks;
      },
      error: (error) => {
        console.error('Error loading tech stacks', error);
        this.errorMessage = 'Failed to load technology stacks';
      }
    });
  }
  
  toggleTechStack(techStackId: number): void {
    const index = this.selectedTechStacks.indexOf(techStackId);
    if (index === -1) {
      this.selectedTechStacks.push(techStackId);
    } else {
      this.selectedTechStacks.splice(index, 1);
    }
  }
  
  isTechStackSelected(techStackId: number): boolean {
    return this.selectedTechStacks.includes(techStackId);
  }

  navigateToMarketplace(): void {
    this.router.navigate(['/marketplace']);
  }
  
  onSubmit(): void {
    console.log('onSubmit called');
    
    if (this.bugForm.invalid) {
      console.log('Form is invalid', this.bugForm.errors);
      // Mark all form controls as touched to trigger validation messages
      Object.keys(this.bugForm.controls).forEach(key => {
        const control = this.bugForm.get(key);
        control?.markAsTouched();
        console.log(`${key} errors:`, control?.errors);
      });
      return;
    }
    
    if (this.selectedTechStacks.length === 0) {
      console.log('No tech stacks selected');
      this.errorMessage = 'Please select at least one technology stack';
      return;
    }
    
    console.log('Form is valid, preparing to submit');
    this.isSubmitting = true;
    this.errorMessage = '';
    this.successMessage = '';
    
    const bugData = {
      ...this.bugForm.value,
      techStackIds: this.selectedTechStacks
    };
    
    console.log('Bug data to submit:', bugData);
    
    this.bugService.createBug(bugData).subscribe({
      next: (response) => {
        console.log('Bug created successfully:', response);
        this.isSubmitting = false;
        this.successMessage = 'Bug created successfully!';
        
        // Reset form
        this.bugForm.reset();
        this.selectedTechStacks = [];
        
        // Redirect to the newly created bug after a short delay
        setTimeout(() => {
          this.router.navigate(['/bugs', response.id]);
        }, 2000);
      },
      error: (error) => {
        console.error('Error creating bug:', error);
        this.isSubmitting = false;
        this.errorMessage = error.error?.message || 'Failed to create bug. Please try again.';
      }
    });
  }
}