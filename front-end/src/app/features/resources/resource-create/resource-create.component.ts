import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ResourceService } from '../../../core/services/resource.service';
import { TokenStorageService } from '../../../core/auth/token.storage';
import { AlertComponent } from '../../../shared/components/alert/alert.component';
import { ButtonComponent } from '../../../shared/components/button/button.component';
import { CardComponent } from '../../../shared/components/card/card.component';
import { LoaderComponent } from '../../../shared/components/loader/loader.component';
import { SidebarLayoutComponent } from '../../../layout/sidebar-layout/sidebar-layout.component';

@Component({
  selector: 'app-resource-create',
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
  templateUrl: './resource-create.component.html',
  styleUrls: ['./resource-create.component.css']
})
export class ResourceCreateComponent implements OnInit {
  resourceForm: FormGroup;
  resourceTypes: string[] = [];
  
  isLoading = false;
  isSubmitting = false;
  errorMessage = '';
  successMessage = '';
  isCompany = false;

  constructor(
    private fb: FormBuilder,
    private resourceService: ResourceService,
    private router: Router,
    private tokenStorage: TokenStorageService
  ) {
    this.resourceForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(200)]],
      description: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(1000)]],
      url: ['', [Validators.required, this.urlValidator]],
      resourceType: ['', Validators.required]
    });
    
    // Check if user is a company
    const userRole = this.tokenStorage.getUserRole();
    this.isCompany = userRole === 'COMPANY';
  }

  ngOnInit(): void {
    if (!this.isCompany) {
      // Redirect if not a company
      this.router.navigate(['/resources']);
      return;
    }
    
    this.loadResourceTypes();
  }
  
  loadResourceTypes(): void {
    this.resourceService.getResourceTypes().subscribe({
      next: (types) => {
        this.resourceTypes = types;
      },
      error: (error) => {
        console.error('Error loading resource types', error);
        this.errorMessage = 'Failed to load resource types';
      }
    });
  }
  
  navigateToResourceList(): void {
    this.router.navigate(['/resources']);
  }
  
  onSubmit(): void {
    if (this.resourceForm.invalid) {
      // Mark all form controls as touched to trigger validation messages
      Object.keys(this.resourceForm.controls).forEach(key => {
        const control = this.resourceForm.get(key);
        control?.markAsTouched();
      });
      return;
    }
    
    this.isSubmitting = true;
    this.errorMessage = '';
    this.successMessage = '';
    
    const resourceData = this.resourceForm.value;
    
    this.resourceService.createResource(resourceData).subscribe({
      next: (response) => {
        this.isSubmitting = false;
        this.successMessage = 'Learning resource created successfully!';
        
        // Reset form
        this.resourceForm.reset();
        
        // Redirect to resources list after a short delay
        setTimeout(() => {
          this.router.navigate(['/resources']);
        }, 2000);
      },
      error: (error) => {
        console.error('Error creating resource:', error);
        this.isSubmitting = false;
        this.errorMessage = error.error?.message || 'Failed to create resource. Please try again.';
      }
    });
  }
  
  // Custom URL validator
  urlValidator(control: any) {
    if (!control.value) {
      return null;
    }
    
    const url = control.value.trim().toLowerCase();
    if (!url.startsWith('http://') && !url.startsWith('https://')) {
      return { invalidUrl: true };
    }
    
    return null;
  }
}