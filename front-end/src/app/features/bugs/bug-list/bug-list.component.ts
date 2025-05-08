import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { BugService } from '../../../core/services/bug.service';
import { Bug } from '../../../core/models/bug.model';
import { CardComponent } from '../../../shared/components/card/card.component';
import { LoaderComponent } from '../../../shared/components/loader/loader.component';
import { PaginationComponent } from '../../../shared/components/pagination/pagination.component';
import { FilterPipe } from '../../../shared/pipes/filter.pipe';
import { DateFormatPipe } from '../../../shared/pipes/date-format.pipe';

@Component({
  selector: 'app-bug-list',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule, 
    CardComponent, 
    LoaderComponent, 
    PaginationComponent,
    FilterPipe,
    DateFormatPipe
  ],
  templateUrl: './bug-list.component.html',
  styleUrls: ['./bug-list.component.css']
})
export class BugListComponent implements OnInit {
  bugs: Bug[] = [];
  difficulties: string[] = [];
  techStacks: any[] = [];
  
  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;
  
  // Filters
  selectedDifficulty: string = '';
  selectedTechStacks: number[] = [];
  searchQuery: string = '';
  selectedStatus: string = '';
  
  // UI state
  isLoading = false;
  errorMessage = '';
  
  constructor(
    private bugService: BugService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadFilters();
    this.loadBugs();
  }
  
  loadFilters(): void {
    // Load difficulty levels
    this.bugService.getDifficulties().subscribe({
      next: (difficulties) => {
        this.difficulties = difficulties;
      },
      error: (error) => {
        console.error('Error loading difficulties', error);
      }
    });
    
    // Load tech stacks
    this.bugService.getTechStacks().subscribe({
      next: (techStacks) => {
        this.techStacks = techStacks;
      },
      error: (error) => {
        console.error('Error loading tech stacks', error);
      }
    });
  }
  
  loadBugs(): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    this.bugService.getBugs(
      this.currentPage,
      this.pageSize,
      this.selectedDifficulty,
      this.selectedTechStacks,
      this.selectedStatus,
      this.searchQuery
    ).subscribe({
      next: (response) => {
        this.bugs = response.content;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading bugs', error);
        this.errorMessage = 'Failed to load bugs. Please try again later.';
        this.isLoading = false;
      }
    });
  }
  
  onPageChange(page: number): void {
    this.currentPage = page - 1; // API is 0-based, UI is 1-based
    this.loadBugs();
  }
  
  applyFilters(): void {
    this.currentPage = 0; // Reset to first page when filters change
    this.loadBugs();
  }
  
  resetFilters(): void {
    this.selectedDifficulty = '';
    this.selectedTechStacks = [];
    this.searchQuery = '';
    this.selectedStatus = '';
    this.currentPage = 0;
    this.loadBugs();
  }
  
  navigateToBugDetail(bugId: number): void {
    this.router.navigate(['/bugs', bugId]);
  }
  
  // Handle tech stack selection (multiple)
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
}