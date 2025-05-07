import { Component, OnInit } from '@angular/core';
import { ResourceService } from '../../../core/services/resource.service';
import { LearningResource } from '../../../core/models/resource.model';

@Component({
  selector: 'app-resource-list',
  templateUrl: './resource-list.component.html',
  styleUrls: ['./resource-list.component.css']
})
export class ResourceListComponent implements OnInit {
  resources: LearningResource[] = [];

  constructor(private resourceService: ResourceService) { }

  ngOnInit(): void {
    // Component initialization
  }
}
