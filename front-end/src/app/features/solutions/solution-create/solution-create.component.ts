import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { SolutionService } from '../../../core/services/solution.service';

@Component({
  selector: 'app-solution-create',
  templateUrl: './solution-create.component.html',
  styleUrls: ['./solution-create.component.css']
})
export class SolutionCreateComponent implements OnInit {
  solutionForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private solutionService: SolutionService
  ) {
    this.solutionForm = this.fb.group({
      description: ['', Validators.required],
      codeLink: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    // Component initialization
  }
}
