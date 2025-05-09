import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Route, Router } from '@angular/router';
import { SolutionService } from '../../../core/services/solution.service';
import { SidebarLayoutComponent } from '../../../layout/sidebar-layout/sidebar-layout.component';
import { BugService } from '../../../core/services/bug.service';
import { AlertComponent } from "../../../shared/components/alert/alert.component";
import { CommonModule, NgIf } from '@angular/common';
import { LoaderComponent } from "../../../shared/components/loader/loader.component";
import { ButtonComponent } from '../../../shared/components/button/button.component';

@Component({
  selector: 'app-solution-create',
  standalone: true,
  templateUrl: './solution-create.component.html',
  imports: [
    ReactiveFormsModule,
    SidebarLayoutComponent,
    AlertComponent,
    ButtonComponent,
    LoaderComponent],
  styleUrls: ['./solution-create.component.css']
})
export class SolutionCreateComponent implements OnInit {
  solutionForm: FormGroup;
  bugId: string;
  bugtitle: string;
  isLoading: boolean = true;
  isSubmitting: boolean = false;
  successMessage: string = '';
  errorMessage: string = '';

  navigateToBugListing() {
    //TODO:
    this.router.navigate(['/bugs/'+this.bugId])
    this.bugId = '';
    this.bugtitle = '';
    this.isLoading = true;
    this.isSubmitting = false;
  }

  onSubmit() {
    // TODO:
    this.successMessage = "Called on submit"
  }

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private bugServices: BugService,
    private solutionService: SolutionService
  ) {
    this.solutionForm = this.fb.group({
      description: ['', Validators.required],
      codeLink: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    // Component initialization
    this.loadBug();
  }

  loadBug(): void {
    this.bugId = this.route.snapshot.paramMap.get('id');
    if (!this.bugId) {
      this.isLoading = false;
      return;
    };

    // TODO: REMOVE THIS CASE
    if (Number(this.bugId) == 1) {
      this.bugtitle = "Hey I am a dummy bug, please kill me after you're done";
      this.isLoading = false;
      return;
    }

    this.bugServices.getBugById(Number(this.bugId)).subscribe({
      next: (bug) => {
        this.isLoading = false;
        this.bugtitle = bug.title;
      },
      error: (error) => {
        this.isLoading = false;
        console.log("error loading bug")
      }
    });
  }
}
