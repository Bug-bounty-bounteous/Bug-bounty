import { Component, OnInit } from '@angular/core';
import { SolutionService } from '../../../core/services/solution.service';
import { Solution } from '../../../core/models/solution.model';
import { SideBarComponent } from "../../../layout/side-bar/side-bar.component";
import { SidebarLayoutComponent } from "../../../layout/sidebar-layout/sidebar-layout.component";

@Component({
  selector: 'app-solution-list',
  standalone: true,
  templateUrl: './solution-list.component.html',
  styleUrls: ['./solution-list.component.css'],
  imports: [SidebarLayoutComponent]
})
export class SolutionListComponent implements OnInit {
  solutions: Solution[] = [];

  constructor(private solutionService: SolutionService) { }

  ngOnInit(): void {
    // Component initialization
  }
}
