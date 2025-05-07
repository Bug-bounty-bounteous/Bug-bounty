import { Component, OnInit } from '@angular/core';
import { UserService } from '../../../core/services/user.service';
import { BugService } from '../../../core/services/bug.service';
import { SolutionService } from '../../../core/services/solution.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  constructor(
    private userService: UserService,
    private bugService: BugService,
    private solutionService: SolutionService
  ) { }

  ngOnInit(): void {
    // Component initialization
  }
}
