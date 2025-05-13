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
  validFile: boolean = false;
  fileTouched: boolean = false;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private bugServices: BugService,
    private solutionService: SolutionService
  ) {
    this.solutionForm = this.fb.group({
      description: ['', Validators.required],
      // codeLink: ['', Validators.required],
      codeLink: [''],
      // file: ['', Validators.required],
      file: ['']
    });
  }

  onFileChange(event: Event) {
    this.solutionForm.patchValue({file: ''});
    let reader = new FileReader();
    this.validFile = false;
    if ((event.target as HTMLInputElement).files && (event.target as HTMLInputElement).files.length)  {
      const [file] = (event.target as HTMLInputElement).files;
      if (file.size > 10_000_000) {
        this.validFile = false;
        this.errorMessage = 'File is too big, max size is 10 MBs, use a code link instead';
        (event.target as HTMLInputElement).value = null;
      } else {
        this.validFile = true;
        reader.readAsText(file);
        reader.onload = () => {
          this.solutionForm.patchValue({
            file: reader.result.toString()
          })
        }

      }
    }
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

    // if (this.bugId == '1') {
    //   this.isLoading = false;
    //   this.bugtitle = 'This is a bug to delete'
    // }


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

  navigateToBugListing() {
    //TODO:
    this.router.navigate(['/bugs/', this.bugId])
    this.bugId = '';
    this.bugtitle = '';
    this.isLoading = true;
    this.isSubmitting = false;
    this.successMessage = '';
    this.errorMessage = '';
    this.solutionForm.reset();
  }

  onSubmit() {
    // TODO:
    if (this.solutionForm.invalid) {
      this.fileTouched = true;
      Object.keys(this.solutionForm.controls).forEach(key => {
        const control = this.solutionForm.get(key);
        control?.markAsTouched();
      });
      return;
    }
    this.isSubmitting = true;
    const data = {
      ...this.solutionForm.value,
      bugId: Number(this.bugId)
    };
    this.solutionService.postSolution(data).subscribe({
      next: (response) => {
        this.isSubmitting = false;
        this.successMessage = 'Solution posted successfully'
        this.solutionForm.reset();
        setTimeout(() => {
          // TODO: Go to solution shower screen
          this.router.navigate(['/marketplace']);
        }, 2000);
      },
      error: (error) => {
        this.isSubmitting = false;
        this.errorMessage = error.error?.message || 'Failed to post solution, please try again later.'
      } 
    })
  }

}
