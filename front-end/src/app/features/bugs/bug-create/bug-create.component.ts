import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BugService } from '../../../core/services/bug.service';

@Component({
  selector: 'app-bug-create',
  templateUrl: './bug-create.component.html',
  styleUrls: ['./bug-create.component.css']
})
export class BugCreateComponent implements OnInit {
  bugForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private bugService: BugService
  ) {
    this.bugForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      difficulty: ['', Validators.required],
      reward: [0, Validators.required]
    });
  }

  ngOnInit(): void {
    // Component initialization
  }
}
