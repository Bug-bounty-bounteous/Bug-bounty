import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BugService } from '../../../core/services/bug.service';
import { Bug } from '../../../core/models/bug.model';

@Component({
  selector: 'app-bug-detail',
  templateUrl: './bug-detail.component.html',
  styleUrls: ['./bug-detail.component.css']
})
export class BugDetailComponent implements OnInit {
  bug?: Bug;

  constructor(
    private route: ActivatedRoute,
    private bugService: BugService
  ) { }

  ngOnInit(): void {
    // Component initialization
  }
}
