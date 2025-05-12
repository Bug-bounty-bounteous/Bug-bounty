import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ResourceService } from '../../../core/services/resource.service';
import { LearningResource } from '../../../core/models/resource.model';
import { TokenStorageService } from '../../../core/auth/token.storage';
import { CardComponent } from '../../../shared/components/card/card.component';
import { LoaderComponent } from '../../../shared/components/loader/loader.component';
import { ButtonComponent } from '../../../shared/components/button/button.component';
import { PaginationComponent } from '../../../shared/components/pagination/pagination.component';
import { AlertComponent } from '../../../shared/components/alert/alert.component';
import { DateFormatPipe } from '../../../shared/pipes/date-format.pipe';
import { SidebarLayoutComponent } from '../../../layout/sidebar-layout/sidebar-layout.component';

@Component({
  selector: 'app-resource-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    CardComponent,
    LoaderComponent,
    ButtonComponent,
    PaginationComponent,
    AlertComponent,
    DateFormatPipe,
    SidebarLayoutComponent
  ],
  templateUrl: './resource-list.component.html',
  styleUrls: ['./resource-list.component.css']
})
export class ResourceListComponent implements OnInit {
  resources: LearningResource[] = [];
  resourceTypes: string[] = [];
  
  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;
  
  // Filters
  selectedType: string = '';
  searchQuery: string = '';
  
  // UI state
  isLoading = false;
  isCompany = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private resourceService: ResourceService,
    private tokenStorage: TokenStorageService,
    private router: Router
  ) {
    // Check if user is a company
    const userRole = this.tokenStorage.getUserRole();
    this.isCompany = userRole === 'COMPANY';
  }

  ngOnInit(): void {
    this.loadResourceTypes();
    this.loadResources();
  }
  
  loadResourceTypes(): void {
    this.resourceService.getResourceTypes().subscribe({
      next: (types) => {
        this.resourceTypes = types;
      },
      error: (error) => {
        console.error('Error loading resource types', error);
      }
    });
  }
  
  loadResources(): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    this.resourceService.getResources(
      this.currentPage,
      this.pageSize,
      this.selectedType,
      this.searchQuery
    ).subscribe({
      next: (response) => {
        this.resources = response.content;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading resources', error);
        this.errorMessage = 'Failed to load resources. Please try again later.';
        this.isLoading = false;
      }
    });
  }
  
  onPageChange(page: number): void {
    this.currentPage = page - 1; // API is 0-based, UI is 1-based
    this.loadResources();
  }
  
  applyFilters(): void {
    this.currentPage = 0; // Reset to first page when filters change
    this.loadResources();
  }
  
  resetFilters(): void {
    this.selectedType = '';
    this.searchQuery = '';
    this.currentPage = 0;
    this.loadResources();
  }
  
  navigateToResourceDetail(resourceId: number): void {
    this.router.navigate(['/resources', resourceId]);
  }
  
  navigateToCreateResource(): void {
    this.router.navigate(['/resources/create']);
  }
  
  reportResource(resourceId: number): void {
    this.resourceService.reportResource(resourceId).subscribe({
      next: () => {
        this.successMessage = 'Resource reported successfully. Thank you for your feedback!';
        // Update the resource in the local array
        const resource = this.resources.find(r => r.id === resourceId);
        if (resource) {
          resource.reported = true;
        }
      },
      error: (error) => {
        console.error('Error reporting resource', error);
        this.errorMessage = 'Failed to report resource. Please try again later.';
      }
    });
  }
  
  openResource(url: string): void {
    window.open(url, '_blank');
  }
  
  getResourceTypeClass(type: string): string {
    switch (type.toLowerCase()) {
      case 'tutorial':
        return 'type-tutorial';
      case 'documentation':
        return 'type-documentation';
      case 'video':
        return 'type-video';
      case 'article':
        return 'type-article';
      case 'code_example':
        return 'type-code';
      default:
        return 'type-default';
    }
  }
}