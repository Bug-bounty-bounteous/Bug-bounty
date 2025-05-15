import { Component, OnInit } from '@angular/core';
import { SolutionService } from '../../../core/services/solution.service';
import { Solution } from '../../../core/models/solution.model';
import { Feedback } from '../../../core/models/feedback.model';

@Component({
  selector: 'app-solution-list',
  templateUrl: './solution-list.component.html',
  styleUrls: ['./solution-list.component.css']
})
export class SolutionListComponent implements OnInit {
  solutions: Solution[] = [];
  feedbacks:Feedback[]; 

  constructor(private solutionService: SolutionService) { }

  ngOnInit(): void {
    // Component initialization
  }
}
