import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ResourceService } from '../../../core/services/resource.service';

@Component({
  selector: 'app-resource-create',
  templateUrl: './resource-create.component.html',
  styleUrls: ['./resource-create.component.css']
})
export class ResourceCreateComponent implements OnInit {
  resourceForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private resourceService: ResourceService
  ) {
    this.resourceForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required],
      url: ['', Validators.required],
      resourceType: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    // Component initialization
  }
}
