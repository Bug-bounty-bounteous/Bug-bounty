import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
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
    FormsModule,
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
  uploadMode = false; // false = URL, true = File upload
  selectedFile: File | null = null;
  uploadProgress = 0;

  constructor(
    private fb: FormBuilder,
    private resourceService: ResourceService,
    private router: Router,
    private tokenStorage: TokenStorageService
  ) {
    this.resourceForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(200)]],
      description: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(1000)]],
      url: ['', [this.urlValidator]],
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
    this.updateValidators();
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
  
  toggleUploadMode(): void {
    this.uploadMode = !this.uploadMode;
    this.selectedFile = null;
    this.errorMessage = '';
    this.updateValidators();
  }
  
  updateValidators(): void {
    const urlControl = this.resourceForm.get('url');
    
    if (this.uploadMode) {
      urlControl?.clearValidators();
    } else {
      urlControl?.setValidators([Validators.required, this.urlValidator]);
    }
    
    urlControl?.updateValueAndValidity();
  }
  
  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      this.errorMessage = '';
      
      // Check file size (10MB limit)
      const maxSize = 10 * 1024 * 1024; // 10MB in bytes
      if (file.size > maxSize) {
        this.errorMessage = 'File size exceeds 10MB limit. Please provide a link to an external source instead.';
        this.selectedFile = null;
        // Reset file input
        (event.target as HTMLInputElement).value = '';
      }
    }
  }
  
  onSubmit(): void {
    // Validate based on mode
    if (this.uploadMode) {
      if (!this.selectedFile) {
        this.errorMessage = 'Please select a file to upload';
        return;
      }
    } else {
      if (this.resourceForm.get('url')?.invalid) {
        this.errorMessage = 'Please provide a valid URL';
        return;
      }
    }
    
    // Check other form fields
    if (this.resourceForm.get('title')?.invalid || 
        this.resourceForm.get('description')?.invalid || 
        this.resourceForm.get('resourceType')?.invalid) {
      
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
    
    if (this.uploadMode) {
      this.submitWithFile();
    } else {
      this.submitWithUrl();
    }
  }
  
  submitWithUrl(): void {
    const resourceData = this.resourceForm.value;
    
    this.resourceService.createResource(resourceData).subscribe({
      next: (response) => {
        this.handleSuccess();
      },
      error: (error) => {
        this.handleError(error);
      }
    });
  }
  
  submitWithFile(): void {
    const formData = new FormData();
    formData.append('file', this.selectedFile!);
    formData.append('title', this.resourceForm.get('title')?.value);
    formData.append('description', this.resourceForm.get('description')?.value);
    formData.append('resourceType', this.resourceForm.get('resourceType')?.value);
    
    this.resourceService.createResourceWithFile(formData).subscribe({
      next: (response) => {
        this.handleSuccess();
      },
      error: (error) => {
        this.handleError(error);
      }
    });
  }
  
  handleSuccess(): void {
    this.isSubmitting = false;
    this.successMessage = 'Learning resource created successfully!';
    
    // Reset form
    this.resourceForm.reset();
    this.selectedFile = null;
    
    // Redirect to resources list after a short delay
    setTimeout(() => {
      this.router.navigate(['/resources']);
    }, 2000);
  }
  
  handleError(error: any): void {
    console.error('Error creating resource:', error);
    this.isSubmitting = false;
    
    if (error.error?.message && error.error.message.includes('File size exceeds')) {
      this.errorMessage = error.error.message;
      // Suggest switching to URL mode
      this.errorMessage += ' Please try using the URL option instead.';
    } else {
      this.errorMessage = error.error?.message || 'Failed to create resource. Please try again.';
    }
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