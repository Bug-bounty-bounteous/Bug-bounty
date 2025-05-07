import { Component, OnInit } from '@angular/core';
import { BugService } from '../../../core/services/bug.service';
import { Bug } from '../../../core/models/bug.model';

@Component({
  selector: 'app-bug-list',
  templateUrl: './bug-list.component.html',
  styleUrls: ['./bug-list.component.css']
})
export class BugListComponent implements OnInit {
  bugs: Bug[] = [];

  constructor(private bugService: BugService) { }

  ngOnInit(): void {
    // Component initialization
  }
}
