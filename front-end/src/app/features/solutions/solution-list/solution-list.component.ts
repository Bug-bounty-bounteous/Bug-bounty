import { Component, OnInit } from '@angular/core';
import { SolutionService } from '../../../core/services/solution.service';
import { Solution } from '../../../core/models/solution.model';
import { SidebarLayoutComponent } from "../../../layout/sidebar-layout/sidebar-layout.component";
import { UserService } from '../../../core/services/user.service';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Bug } from '../../../core/models/bug.model';
import { User } from '../../../core/models/user.model';
import { LoaderComponent } from "../../../shared/components/loader/loader.component";
import { BugService } from '../../../core/services/bug.service';
import { AlertComponent } from "../../../shared/components/alert/alert.component";

@Component({
  selector: 'app-solution-list',
  standalone: true,
  templateUrl: './solution-list.component.html',
  styleUrls: ['./solution-list.component.css'],
  imports: [SidebarLayoutComponent,
    CommonModule, LoaderComponent, AlertComponent]
})
export class SolutionListComponent implements OnInit {
  solutions: Solution[] = [];
  claimerId: number;
  isLoading: boolean = true;
  errorMessage: string = '';


  constructor(
    private solutionService: SolutionService,
    private bugService: BugService,
    private route: ActivatedRoute,
    private router: Router
  ) { 
  }

  ngOnInit(): void {
    this.loadDetails();
  }

  loadDetails() {
    this.isLoading = true;
    this.loadSolutionsClaimerId();
  }

  loadSolutionsClaimerId() {
    let bugId = Number(this.route.snapshot.paramMap.get("id"))
    this.solutionService.getSolutionsForBug(bugId).subscribe(
      {
        next: (response) => {
          this.solutions = response;
          this.loadClaimerId();
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.error?.message || error.error?.error?.message || "Failed to load solutions";
        }
      }
    );
  }

  loadClaimerId() {
    let bugId = Number(this.route.snapshot.paramMap.get("id"))
    this.bugService.getBugClaimerId(bugId).subscribe(
      {
        next: (response) => {
          this.claimerId = response;
          this.isLoading = false;
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.error?.message || error.error?.error?.message || "Failed to load claimerId";
        }
      }
    );
  }

  sendOpened(solutionId: number) {
    this.router.navigate(["/", "solutions", solutionId]);
  }

  isByCurrentClaimer(developerId: number): any {
    return developerId == this.claimerId;
  }
}